# linbox_server

LINBOX is a im system designed for www.medtree.cn.

Download medtree app for iphone at [here](https://itunes.apple.com/cn/app/yi-shu/id933709180?mt=8)

Download medtree app for android at [here](https://medtree.cn/release/android/4.0.0/medtree.apk)


# Dependencies
## Java Version
**JDK-1.8** is required to build and run it.

## Security Update For JRE
**Be carefual:** as we use AES-256 encryption, you need to download packages to update your local jre environment.
* Download package at [here](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
* Unzip and copy the jars into ```${java.home}/jre/lib/security/```

Click [this](http://stackoverflow.com/questions/6481627/java-security-illegal-key-size-or-default-parameters) for detail information for this problem.

## Mysql
Mysql database is used as persistence storage for im messages.

### Mysql Versions
There is no special requirement for mysql version.
But if you want to use **emojis**, you need to use mysql version **5.6+**, and config the character set to *utf8mb4*.

### Predefined Tables
You can find all mysql operations in package ```com.linbox.im.server.storage```

There are 4 tables predefined in programs, sql script could be find in [docs/mysql.sql](src/main/docs/mysql.sql)
* 
 



