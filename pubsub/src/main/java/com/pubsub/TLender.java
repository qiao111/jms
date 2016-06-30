package com.pubsub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 向主题中发布一个新的利率  发布者
 * @author qiaolin
 *
 */
public class TLender {
	
	private TopicConnection topicConnection;
	
	private Topic topic;
	
	private TopicSession topicSession;
	
	public TLender(String topicFactory,String topicName){
		try {
			//连接JMS提供者 并创建连接
			Context context = new InitialContext();
			TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) context.lookup(topicFactory);
			topicConnection = topicConnectionFactory.createTopicConnection();
			//创建JMS会话
			topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			//获取主题
			topic = (Topic)context.lookup(topicName);
			//启动连接
			topicConnection.start();
		} catch (NamingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (JMSException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * 发布利率
	 * @param rate
	 */
	private void publishRate(double rate){
		try {
			BytesMessage message = topicSession.createBytesMessage();
			message.writeDouble(rate);//写入利率
			//创建发布者 并发布消息
			TopicPublisher publisher = topicSession.createPublisher(topic);
			publisher.publish(message);
		} catch (JMSException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 退出
	 */
	private void exit(){
		try {
			topicConnection.close();
		} catch (JMSException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		if(args.length != 2){
			System.out.println("参数不合法");
			System.exit(1);
		}
		String topicFactory = args[0],topicName = args[1];
		try{
			TLender lender = new TLender(topicFactory,topicName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				String rate = reader.readLine();
				if(rate == null || rate.trim().length() <=0){
					lender.exit();
				}
				//发布利率
				lender.publishRate(Double.valueOf(rate));
			}
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
}
