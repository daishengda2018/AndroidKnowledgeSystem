> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 [mthli.xyz](https://mthli.xyz/rxjava-scheduler/)

> 通过设置不同的调度器，可以灵活地在不同线程间切换。

RxJava 在链式调用的设计基础上，通过设置不同的调度器，可以灵活地在不同线程间切换并执行对应的 Task。让我们一起来了解一下这种切换模式是如何实现的。

Scheduler
---------

[Scheduler](http://reactivex.io/RxJava/javadoc/io/reactivex/Scheduler.html) 是所有 RxJava 调度器的抽象父类，子类需要复写其 `createWorker()` 返回一个 [Worker](http://reactivex.io/RxJava/javadoc/io/reactivex/Scheduler.Worker.html) 实例，用来接受并执行 Task；同时也可以复写其 `scheduleDirect()` 来决定如何将 Task 分配给不同的 Worker。一个缩略版的 Scheduler 源码如下：

```java
public abstract class Scheduler {
  ...

  @NonNull
  public abstract Worker createWorker();

  // 调度一次定时 Task，细节封装在传入的 Runnable 里
  @NonNull
  public Disposable scheduleDirect(
    @NonNull Runnable run, long delay, @NonNull TimeUnit unit
  ) {
    // 新建一个 Worker
    final Worker w = createWorker();

    // 静态代理并封装我们想要执行的 Runnable，具体实现可忽略
    final Runnable decoratedRun
      = RxJavaPlugins.onSchedule(run);
    DisposeTask task = new DisposeTask(decoratedRun, w);

    // 将 Task 交给新建的 Worker 执行
    w.schedule(task, delay, unit);
    return task;
  }

  // 同时 Worker 也是一个抽象类
  public abstract static class Worker implements Disposable {
    ...

    // 执行被分配的定时 Task；
    // 注意，Worker 内部也可以维护一个自己的 Task 调度策略
    @NonNull
    public abstract Disposable schedule(
      @NonNull Runnable run, long delay,
      @NonNull TimeUnit unit);
  }
}
```

总的来说，Scheduler 的默认实现为：只要有新 Task 到来，就新建一个 Worker 实例并将 Task 分配给它；同时 Worker 内部也可以维护一个自己的 Task 调度策略。

newThread
---------

RxJava 的 [newThread](http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/Schedulers.html#newThread--) 调度器对每一个新 Task 都会新起一个线程去执行它。我们以 newThread 为例，看看一个最简单的 Scheduler 是怎样实现的。

我们平时使用的 `Schedulers.newThread()` 是一个返回 NewThreadScheduler 实例的单例模式。以下是 NewThreadScheduler 对应的源码：

```java
public final class NewThreadScheduler extends Scheduler {
  final ThreadFactory threadFactory;

  ...

  @NonNull
  @Override
  public Worker createWorker() {
    return new NewThreadWorker(threadFactory);  }
}
```

可以看到，NewThreadScheduler 没有复写 `scheduleDirect()` 的默认行为，即「只要有新 Task 到来，就新建一个 Worker 实例并将 Task 分配给它」；它仅仅是复写了 `createWorker()` 返回了一个具体的 NewThreadWorker 实例。

我们再来看看 NewThreadWorker 对应的源码：

```java
public class NewThreadWorker extends Scheduler.Worker
  implements Disposable {

  private final ScheduledExecutorService executor;

  public NewThreadWorker(ThreadFactory threadFactory) {
    // 最终被赋值为 Executors.newScheduledThreadPool(1)    executor = SchedulerPoolFactory.create(threadFactory);  }

  @NonNull
  @Override
  public Disposable schedule(
    @NonNull final Runnable action, long delayTime,
    @NonNull TimeUnit unit
  ) {
    ...
    return scheduleActual(action, delayTime, unit, null);
  }

  @NonNull
  public ScheduledRunnable scheduleActual(
    final Runnable run, long delayTime,
    @NonNull TimeUnit unit,
    @Nullable DisposableContainer parent
  ) {
    // 静态代理并封装我们想要执行的 Runnable，具体实现可忽略
    Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
    ScheduledRunnable sr =
      new ScheduledRunnable(decoratedRun, parent);

    ...

    Future<?> f;
    try {
      if (delayTime <= 0) {        // 立即执行        f = executor.submit((Callable<Object>) sr);      } else {        // 延时调度        f = executor.schedule(          (Callable<Object>) sr, delayTime, unit);      }      sr.setFuture(f);
    } catch (RejectedExecutionException ex) {
      ...
    }

    return sr;
  }
}
```

结合 NewThreadScheduler 和 NewThreadWorker 的源码可以看到，每一个新的 Task 都会被一个新建的线程池容量为 1 的 [ScheduledExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html) 立即执行或延时调度，这是 JDK 原生提供的一个多线程调度器实现。其他的 RxJava 调度器实现在这里就不展开了，感兴趣的同学可以自行查阅对应的源码。

链式调用
----

在了解了 Scheduler 的具体实现后，我们还需要知道 Scheduler 是如何在链式调用中工作的。关于 RxJava 的链式调用是如何工作的，建议先阅读笔者之前的文章 [RxJava 链式调用原理](https://mthli.xyz/rxjava-chain/)，在此不予赘述。这里我们主要讲解用于线程切换的 [subscribeOn](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#subscribeOn-io.reactivex.Scheduler-) 和 [observeOn](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#observeOn-io.reactivex.Scheduler-) 两个操作符。注意，本文均以 [Observable](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html) 的操作符实现作为讨论对象。

subscribeOn 用于设置 Observable 开始执行时所在的线程；observeOn 用于设置从该操作符调用处开始下游操作符所在的线程。一个典型的线程切换场景如下：

```
Observable
  .create(...) // 在 io 调度器上执行
  .subscribeOn(Schedulers.io())
  .observeOn(AndroidSchedulers.mainThread())
  .subscribe(...) // 在 Android 主线程上执行
```

我们先看看 subscribeOn 对应的源码：

```java
public abstract class Observable<@NonNull T>
  implements ObservableSource<T> {
  ...

  @NonNull
  public final Observable<T> subscribeOn(
    @NonNull Scheduler scheduler
  ) {
    ...
    return RxJavaPlugins.onAssembly(
      new ObservableSubscribeOn<>(this, scheduler)    );
  }
}
```

再来看看 ObservableSubscribeOn 对应的源码：

```java
public final class ObservableSubscribeOn<T>
  extends AbstractObservableWithUpstream<T, T> {

  final Scheduler scheduler;

  public ObservableSubscribeOn(
    ObservableSource<T> source, Scheduler scheduler
  ) {
    super(source);
    this.scheduler = scheduler;
  }

  @Override
  public void subscribeActual(
    final Observer<? super T> observer
  ) {
    // 静态代理传入的上游 Observer，具体实现可忽略
    final SubscribeOnObserver<T> parent =
      new SubscribeOnObserver<>(observer);

    ...

    parent.setDisposable(
      scheduler.scheduleDirect(new SubscribeTask(parent))    );
  }

  ...

  final class SubscribeTask implements Runnable {
    private final SubscribeOnObserver<T> parent;

    SubscribeTask(SubscribeOnObserver<T> parent) {
      this.parent = parent;
    }

    // 这是 Runnable 接口必须实现的方法，    // 使得 subscribe() 可以运行在对应的 Scheduler    @Override    public void run() {      // source 对象是上游的 Observable,      // parent 对象是下游的 Observer      source.subscribe(parent);    }  }
}
```

可以看到，subscribeOn 通过将 Observable 的 `subscribe()` 封装在 Task 中，并调用 Scheduler 的 `scheduleDirect()` 进行线程切换，从而达到「设置 Observable 开始执行时所在的线程」的目的。

接着我们看看 observeOn 对应的源码：

```java
public abstract class Observable<@NonNull T>
  implements ObservableSource<T> {
  ...

  @NonNull
  public final Observable<T> observeOn(
    @NonNull Scheduler scheduler,
    boolean delayError, int bufferSize
  ) {
    ...
    return RxJavaPlugins.onAssembly(
      new ObservableObserveOn<>(        this, scheduler, delayError, bufferSize)    );
  }
}
```

再来看看 ObservableObserveOn 对应的源码：

```java
public final class ObservableSubscribeOn<T>
  extends AbstractObservableWithUpstream<T, T> {

  final Scheduler scheduler;

  public ObservableObserveOn(
    ObservableSource<T> source, Scheduler scheduler,
    boolean delayError, int bufferSize
  ) {
    super(source);
    this.scheduler = scheduler;
    ...
  }

  @Override
  public void subscribeActual(
    final Observer<? super T> observer
  ) {
    if (scheduler instanceof TrampolineScheduler) {
      ...
    } else {
      // 直接创建一个新的 Worker 实例      Scheduler.Worker w = scheduler.createWorker();      // source 对象是上游的 Observable，      // observer 对象是下游的 Observer；      // 此处通过创建一个 ObserveOnObserver 作为中间人角色，      // 它订阅了 source 并在相关回调中调用 observer 的对应方法,      // 仍然是静态代理模式的应用      source.subscribe(new ObserveOnObserver<>(        observer, w, delayError, bufferSize));    }
  }

  ...

  static final class ObserveOnObserver<T>
    extends BasicIntQueueDisposable<T>
    implements Observer<T>, Runnable {
    ...

    final Observer<? super T> downstream;
    final Scheduler.Worker worker;

    ObserveOnObserver(
      Observer<? super T> actual, Scheduler.Worker worker,
      boolean delayError, int bufferSize
    ) {
      this.downstream = actual;      this.worker = worker;
      ...
    }

    // 和平时调用 subscribe() 时 new Observer 一样，
    // 复写以下四个方法；具体实现相对复杂，略去不表
    @Override
    public void onSubscribe(Disposable d) { ... }

    @Override
    public void onNext(T t) { ... }

    @Override
    public void onError(Throwable t) { ... }

    @Override
    public void onComplete() { ... }

    // 主要调用 downstream 的逻辑在这里；    // 这是 Runnable 接口必须实现的方法，    // 使得 downstream 可以运行在对应的 Scheduler    @Override    public void run() { ... }
    // 实际的逻辑跳转很多，但最终在这里切换线程
    void schedule() {
      if (getAndIncrement() == 0) {
        worker.schedule(this);      }
    }

    ...
  }
}
```

可以看到，observeOn 通过将调用下游 Observer 的调用逻辑封装在 Task 中，由指定的 Worker 实例进行线程切换，从而达到了「设置从该操作符调用处开始下游操作符所在的线程」的目的。

看到这里，你可能也注意到了：从之前 Scheduler 的源码我们可知，默认情况下调用 `scheduleDirect()` 也是将 Task 交给 `createWorker()` 新建的 Worker 实例执行的；那为什么 observeOn 要采取和 subscribeOn 不同的实现方式呢？感兴趣的同学可以去看看 [single](http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/Schedulers.html#single--) 调度器的源码，分开两个方法可以更充分的自定义，且这两个方法也不一定是直接相关的。只要保证底层的调度逻辑是正确的就 OK 了。

总的来说，subscribeOn 和 observeOn 都是将逻辑封装到 Runnable 中交给对应的 Scheduler 执行，从而实现了线程切换。但受限于篇幅原因，其中仍然有非常多的细节被本文略去了，建议感兴趣的读者可自行查阅源码。

最后，即使在 2020 年的今天，同为 JVM 系语言的 [Kotlin](https://kotlinlang.org/) 已经支持协程的情况下，RxJava 仅仅使用 JDK 提供的多线程 API 就能将线程切换处理的如此优雅，仍然是十分值得学习和使用的库。笔者认为它并没有过时。