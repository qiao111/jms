1、本例子中聊天室使用的消息队列是ActiveMQ。

2、在linux下安装ActiveMQ
	
   下载地址：http://activemq.apache.org/activemq-590-release.html。

	解压后直接运行bin下的activemq start 即可启动。

	打开console,地址为：localhost:8161/admin，此时需要登录用户名，在conf/users.properties。

	默认的服务器端口是61616，可以修改，conf/activemq.xml 

3、本例子中测试在window环境下，使用虚拟机下的ActiveMQ(Linux)作为消息中间件。