package com.p2p.group;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 消息组 
 * @author qiaolin
 *
 */
public class JMSSender {
	private QueueConnection connection ;
	
	private QueueSession session;
	
	private QueueSender sender;
	
	public JMSSender(String queueFactory,String queueName){
		try {
			Context context = new InitialContext();
			QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(queueFactory);
			connection = factory.createQueueConnection();
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue)context.lookup(queueName);
			connection.start();
			sender = session.createSender(queue);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 在消息组内发送一条TextMessage消息
	 * @param text
	 * @throws JMSException
	 */
	private void sendMessage(String text) throws JMSException{
		TextMessage message = session.createTextMessage(text);
		message.setStringProperty("JMSXGroupID", "Group");//设置消息组
		sender.send(message);
	}
	
	/**
	 * 发送一条包含序列标记的空有效负载消息
	 * @param marker
	 * @throws JMSException
	 */
	private void sendSequenceMarker(String marker) throws JMSException{
		BytesMessage message = session.createBytesMessage();
		message.setStringProperty("SequenceMarker", marker);
		message.setStringProperty("JMSXGroupID", "Group");
		sender.send(message);
	}
	
	/**
	 * 发送消息
	 */
	public void sendMessageGroup(){
		try {
			sendSequenceMarker("START_SEQUENCE");
			sendMessage("first message");
			sendMessage("second message");
			sendMessage("third message");
			sendSequenceMarker("END_SEQUENCE");
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		JMSSender sender = new JMSSender("QueueFactory","RequestGroup");
		sender.sendMessageGroup();
		System.exit(1);
	}
}
