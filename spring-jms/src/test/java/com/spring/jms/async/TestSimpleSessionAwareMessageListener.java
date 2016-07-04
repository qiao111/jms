package com.spring.jms.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * 测试请求/应答模式
 * @author qiaolin
 *
 */
public class TestSimpleSessionAwareMessageListener {
	
	private Message sendMessage;
	
	@Test
	public void testSend() throws JMSException{
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate)context.getBean("jmsTemplate");
		final Destination destination = (Destination) context.getBean("queueDestination");
		//设置消息属性
		MessagePostProcessor postProcessor = new MessagePostProcessor() {
			public Message postProcessMessage(Message message) throws JMSException {
				message.setJMSReplyTo(destination);//设置响应地址
				sendMessage = message;
				return message;
			}
		};
		//发送消息
		jmsTemplate.convertAndSend((Object)"This is a reply message", postProcessor);
		//等待响应
		String messageSelector = "JMSCorrelationID='" + sendMessage.getJMSMessageID() + "'";
		System.out.println(messageSelector);
		Message message = jmsTemplate.receiveSelected("not_default", messageSelector);
		if(message instanceof TextMessage){
			System.out.println(((TextMessage)message).getText());
		}else{
			System.out.println("no message reply");
		}
	}
	
	@Test
	public void testReceiver() throws IOException{
		new ClassPathXmlApplicationContext("classpath:applicationContext-jms-async.xml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		reader.readLine();
		System.exit(1);
	}
}
