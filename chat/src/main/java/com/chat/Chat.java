package com.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 聊天室
 * @author qiaolin
 *
 */
public class Chat implements MessageListener{
	
	private TopicSession pubSession;
	
	private TopicPublisher publisher;
	
	private TopicConnection connection;
	
	private String username;
	
	public Chat(String topicFactory,String topicName,String username) throws NamingException, JMSException {
		//使用jndi.properties获取一个JNDI链接 默认jndi.properties
		InitialContext context = new InitialContext();
		//查找jms连接工厂并创建连接
		TopicConnectionFactory topicConnFactory = (TopicConnectionFactory) context.lookup(topicFactory);
		connection = topicConnFactory.createTopicConnection();
		
		//创建jms会话对象
		pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//查找主题
		Topic chatTopic = (Topic) context.lookup(topicName);
		
		//创建jms发布者和消费者
		publisher = pubSession.createPublisher(chatTopic);
		//创建消费者 附加的参数一个是消息选择器(null),noLocal标记为true表示发布者不能自己消费 为本地测试方便 采用false
		TopicSubscriber subscriber = subSession.createSubscriber(chatTopic, null, true);
		subscriber.setMessageListener(this);//消费者的消息监听器
		
		this.username = username;
		//启动jms链接，允许传送消息
		connection.start();
	}
	
	/**
	 * 接受来自TopicSubscriber的消息
	 */
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println(textMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发布者写消息
	 * @param text
	 * @throws JMSException 
	 */
	protected void writeMessage(String text) throws JMSException{
		TextMessage message = pubSession.createTextMessage();
		message.setText(username + ":" + text);//写消息
		publisher.publish(message);//发布消息
	}
	
	/**
	 * 关闭连接
	 * @throws JMSException
	 */
	public void close() throws JMSException{
		connection.close();
	}
	
	public static void main(String[] args) throws NamingException, JMSException, IOException {
		if(args.length != 3){
			System.out.println("参数不正确,factory,topic,username不能为空！");
			return;
		}
	/*	String username = "zhangsan";
		String topicFactory = "TopicFac";
		String topicName = "topic";*/
		String topicFactory = args[0];
		String topicName = args[1];
		String username = args[2];
		Chat chat = new Chat(topicFactory, topicName, username);//创建聊天
		BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			String content = commandLine.readLine();
			if("exit".equalsIgnoreCase(content)){
				chat.close();
				System.exit(0);
			}else{
				chat.writeMessage(content);//发送消息
			}
		}
	}
}
