package com.pubsub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 订阅者 订阅主题消息  持久型订阅者
 * @author qiaolin
 *
 */
public class TDurableBorrower implements MessageListener{

	private TopicConnection topicConnection;
	
	private TopicSession topicSession;
	
	private Topic topic;
	
	private double currentRate;
	
	private TopicSubscriber subscriber;
	
	public TDurableBorrower(String topicFactory,String topicName,double rate){
		try {
			//连接MQ提供者
			Context context = new InitialContext();
			//获取连接工厂
			TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)context.lookup(topicFactory);
			//创建连接
			topicConnection = topicConnectionFactory.createTopicConnection();
			//设置客户端ID 以供后续创建持久型订阅者
			topicConnection.setClientID("durable");
			//创建JMS会话
			topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			//获取主题
			topic = (Topic)context.lookup(topicName);
			//当前利率
			this.currentRate = rate;
			//启动连接
			topicConnection.start();
			//创建持久型订阅者
			subscriber = topicSession.createDurableSubscriber(topic, "durableSubscriber");
			subscriber.setMessageListener(this);//消息监听器
		} catch (NamingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (JMSException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 消息监听
	 */
	public void onMessage(Message message) {
		try {
			BytesMessage bytesMessage = (BytesMessage)message;
			double rate = bytesMessage.readDouble();
			if(currentRate - rate > 1.0){
				System.out.println("New rate = " + rate + " - Consider refinancing loan ");
			}else{
				System.out.println("New rate = " + rate + " - Keep existing loan");
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 退出
	 */
	private void exit(){
		try {
			subscriber.close();
			//取消订阅状态  对持久型订阅者来说 
			topicSession.unsubscribe("durableSubscriber");
			topicConnection.close();
		} catch (JMSException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		if(args.length != 3){
			System.out.println("参数错误");
			System.exit(1);
		}
		String topicFactory = args[0],topicName = args[1];
		double rate = Double.valueOf(args[2]);
		TDurableBorrower borrower = new TDurableBorrower(topicFactory, topicName, rate);
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			reader.readLine();
			borrower.exit();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
