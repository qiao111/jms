package com.spring.jms.sync;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

/**
 * 主要测试同步接收者
 * @author qiaolin
 *
 */
public class SimpleJmsReceiver {
	
	/**
	 * 测试发送方法
	 * @throws JMSException
	 */
	@Test
	public void testReceive() throws JMSException{
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		//接受默认消息队列中的消息
		Message message = jmsTemplate.receive();
		if(message instanceof TextMessage){
			System.out.println("receive:" + ((TextMessage)message).getText());
		}else{
			System.out.println("no message ");
		}
	}
	
	/**
	 * 接收默认队列中的消息并转换
	 */
	@Test
	public void testReceiveAndConvert(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		//接收默认队列中的消息并转换
		Object message = jmsTemplate.receiveAndConvert();
		if(message instanceof String){
			System.out.println("receive:" + message);
		}else{
			System.out.println("no message");
		}
	}
	
	/**
	 * 使用消息选择器
	 * @throws JMSException 
	 */
	@Test
	public void testReceiverAndSelector() throws JMSException{
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		//接手非默认队列中 优先级大于5的消息
		Message message = jmsTemplate.receiveSelected("not_default", "JMSPriority>=4");
		if(message instanceof TextMessage){
			System.out.println("receive:" + ((TextMessage)message).getText());
		}else{
			System.out.println("no message ");
		}
	}
	
}
