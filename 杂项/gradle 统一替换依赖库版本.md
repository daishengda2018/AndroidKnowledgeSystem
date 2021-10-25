

在项目 build.gradle 追加 ，写着最后就可以

```groovy
def supportVer = '28.0.0'

subprojects {
    project.configurations.all {
        resolutionStrategy.eachDependency { details ->
            if (details.requested.group == 'com.android.support'
                    && !details.requested.name.contains('multidex') ) {
                details.useVersion supportVer
            }
         }
     } 
}     
```

