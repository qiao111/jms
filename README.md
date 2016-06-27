1、书本：java消息服务(第二版)。

2、由于jms的jar包使用maven没法下载，因此，在此手动下载jms的jar包后手动安装到本地仓库中。

	下载地址：http://repository.jboss.org/maven2/javax/jms/jms/1.1/
	
	安装到本地仓库：mvn install:install-file -DgroupId=javax.jms -DartifactId=jms-Dversion=1.1 -Dpackaging=jar -Dfile=E:\jms-1.1.jar
