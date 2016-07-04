package com.spring.jms.async;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class SimpleMessageListener implements MessageListener{

	public void onMessage(Message message) {
		if(message instanceof TextMessage){
			try {
				System.out.println(((TextMessage)message).getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("no message");
		}
	}
}
