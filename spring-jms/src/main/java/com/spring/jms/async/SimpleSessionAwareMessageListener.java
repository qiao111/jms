package com.spring.jms.async;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.listener.SessionAwareMessageListener;

/**
 * 可以访问异步消息侦听器中的JMS的session对象,
 * @author qiaolin
 *
 */
public class SimpleSessionAwareMessageListener implements SessionAwareMessageListener<Message>{

	public void onMessage(Message message, Session session) throws JMSException {
		if(message instanceof TextMessage){
			String text = ((TextMessage)message).getText();
			System.out.println(text);
			MessageProducer sender = session.createProducer(message.getJMSReplyTo());
			TextMessage textMessage = session.createTextMessage();
			textMessage.setJMSCorrelationID(message.getJMSMessageID());
			textMessage.setText("Message " + message.getJMSMessageID() + "received" );
			sender.send(textMessage);
		}
	}

}
