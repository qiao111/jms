package com.p2p.transaction;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 事务性会话
 * @author qiaolin
 *
 */
public class JMSTransactionSender {
	
	private QueueConnection connection;
	
	private QueueSession session;
	
	private QueueSender sender;
		
	public JMSTransactionSender(String queueFactoryName,String queueName){
		try {
			Context context = new InitialContext();
			QueueConnectionFactory queueFactory = (QueueConnectionFactory)context.lookup(queueFactoryName);
			connection = queueFactory.createQueueConnection();
			//创建事务性会话
			session = connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue)context.lookup(queueName);
			connection.start();
			sender = session.createSender(queue);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessageGroup(){
		try {
			sendSequenceMessage("START_SEQUENCE");
			sendTextMessage("first message");
			sendTextMessage("second message");
			sendTextMessage("third message");
			sendSequenceMessage("END_SEQUENCE");
			session.commit();//提交事务
		} catch (JMSException e) {
			e.printStackTrace();
			try {
				session.rollback(); //事务回滚
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void sendTextMessage(String text) throws JMSException {
		TextMessage message = session.createTextMessage(text);
		message.setStringProperty("JMSXGroupID","Group");
		sender.send(message);
	}

	private void sendSequenceMessage(String marker) throws JMSException {
		StreamMessage message = session.createStreamMessage();
		message.setStringProperty("Sequence", marker);
		message.setStringProperty("JMSXGroupID","Group");
		sender.send(message);
	}
	
	public static void main(String[] args) {
		JMSTransactionSender sender = new JMSTransactionSender("QueueFactory", "RequestGroup");
		sender.sendMessageGroup();
		System.exit(1);
	}
}
