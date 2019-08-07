# Checkstyle 常见问题

* Name 'xxx' must match pattern 'xxx'.

类型、函数、字段名不符合规范。我们的规范为:

1. 类名首字母为大写，后续为驼峰命名法. 例如 NewsDogFragment、MainActivity;
2. 函数名以小写字母开头，后续为驼峰命名法，例如 showUserProfile、uploadImage;
3. 普通字段名为m开头，后续为驼峰命名法，例如 mTitle。实体类中的字段可以不以m开头，直接已小写字母开头，后续为驼峰命名法，例如 content、name;
4. 静态字段以s开头，后续为驼峰命名法。例如 sInstance、sCache;
5. 常量全部为大写,如果有多个字母则以下划线分割。例如 MAX_RETRY_COUNT。


* `'if' construct must use '{}'s.	`

if 语句没有使用 大括号 括起来. 注意，if、else、for、while等语句都需要用 大括号括起来.

```
if ( condition ) {
	// do sth
} else {
	// do sth
}
```

* `String literal expressions should be on the left side of an equals comparison.`

问题代码: 

```
String newsType = "mock-type";
if (newsType.equals("videos")) {
	// 
}

```
问题在于在调用字符串的 equals函数时将字符串直接作为equals参数。修改为


```
String newsType = "mock-type";
if ("videos".equals(newsType)) {
	// 
}
```
即可。

* `'&&' should be on a new line.`

这个问题是由于在很长条件语句分割时, 操作符(包括 && 、||、+等操作符)不能作为最后一个字符. 例如: 

```
if( condition1 && 
	condition2 || 
	condition3 ) {
	//
}
```

需要修改为:

```
if( condition1 
	&& condition2 
	|| condition3 ) {
	//
}
```

* `Missing a Javadoc comment.`

类型缺乏注释. 在每个类型、接口定义的地方添加注释即可。 **这个问题经常会出现在定义了接口没有写注释的地方**