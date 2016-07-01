package com.p2p.transaction;

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

public class JMSTransactionReceiver implements MessageListener{
	
	private QueueSession session;
	
	private QueueConnection connection;
	
	private List<String> messageList = new ArrayList<String>();
	
	public JMSTransactionReceiver(String queueFactoryName,String queueName){
		try {
			Context context = new InitialContext();
			QueueConnectionFactory factory = (QueueConnectionFactory)context.lookup(queueFactoryName);
			Queue queue = (Queue)context.lookup(queueName);
			connection = factory.createQueueConnection();
			session = connection.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);
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
			//获取session是否为事务性会话 
			System.out.println("Transaction:" + session.getTransacted());
			if(message.propertyExists("Sequence")){
				String sequence = message.getStringProperty("Sequence");
				if("START_SEQUENCE".equals(sequence)){
					if(message.getJMSRedelivered()){//是否重新发送
						processCompensatingTransaction();
					}
					messageList.clear();
				}else if("END_SEQUENCE".equals(sequence)){
					System.out.println("Message:");
					for(String msg:messageList){
						System.out.println(msg);
					}
					message.acknowledge();//通知服务器确认消息传送
					session.commit();//此处必须进行会话提交,否则接收者接受消息失败，导致重新发送
				}
			}else{
				if(message instanceof TextMessage){
					TextMessage msg = (TextMessage)message;
					processInterimMessage(msg.getText());
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void processInterimMessage(String text) {
		messageList.add(text);
	}

	private void processCompensatingTransaction() {
		messageList.clear();
	}
	
	public static void main(String[] args) {
		new JMSTransactionReceiver("QueueFactory", "RequestGroup");
//		System.exit(1);
	}
}
