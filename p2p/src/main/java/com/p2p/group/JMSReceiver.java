package com.p2p.group;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSReceiver implements MessageListener{
	
	private List<String> messageBuffer = new ArrayList<String>();
	
	public JMSReceiver(String queueFactoryName,String queueName){
		try {
			Context context = new InitialContext();
			QueueConnectionFactory queueFactory = (QueueConnectionFactory)context.lookup(queueFactoryName);
			QueueConnection connection =  queueFactory.createQueueConnection();
			QueueSession session =  connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
			Queue queue = (Queue)context.lookup(queueName);
			connection.start();
			QueueReceiver receiver = session.createReceiver(queue);
			receiver.setMessageListener(this);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	
	public void onMessage(Message message) {
		try {
			if(message.propertyExists("SequenceMarker")){
				String marker = message.getStringProperty("SequenceMarker");
				if("START_SEQUENCE".equals(marker)){
					//检查第一条消息 是否是重新传送的消息
					if(message.getJMSRedelivered()){
						processCompensatingTransaction();
					}
					//当启动一个消息组 清楚消息缓冲器
					messageBuffer.clear();
				}
				if("END_SEQUENCE".equals(marker)){
					System.out.println("Message:");
					for(String msg:messageBuffer){
						System.out.println(msg);
					}
					//通知JMS服务器已经接收到消息
					message.acknowledge();
				}
			}else{
				if(message instanceof TextMessage){
					TextMessage msg = (TextMessage)message;
					processInterimMessage(msg.getText());
				}
				System.out.println("waiting for message ....");
			}
		} catch (JMSException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void processCompensatingTransaction() {
		messageBuffer.clear();
	}

	private void processInterimMessage(String text) {
		//处理临时消息
		messageBuffer.add(text);
	}
	
	public static void main(String[] args) {
		new JMSReceiver("QueueFactory","RequestGroup");
		System.exit(1);
	}

}
