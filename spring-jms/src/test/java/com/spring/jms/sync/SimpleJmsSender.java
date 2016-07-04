package com.spring.jms.sync;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * 此处只要是测试同步发送消息
 * @author qiaolin
 *
 */
public class SimpleJmsSender {

	/**
	 * 测试使用send方法
	 */
	@Test
	public void testSend(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage();
				message.setJMSPriority(9);
				message.setText("This is send function");
				return message;
			}
		};
		jmsTemplate.send(messageCreator);
	}
	
	/**
	 * 发送到不是默认的消息队列
	 */
	@Test
	public void testSendNotDefault(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		MessageCreator creator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage();
				message.setJMSPriority(5);
				message.setText("This is a not default send");
				return message;
			}
		};
		jmsTemplate.send("not_default", creator);
	}

	
	/**
	 * 测试转换后并发送  自动转转消息类型
	 * convert会根据传递参数的类型自动转换成对应的消息类型
	 * 
	 */
	@Test
	public void testConvertAndSend(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		jmsTemplate.convertAndSend("This is convert send");
	}
	
	/**
	 * 自动转换消息并添加消息属性
	 */
	@Test
	public void testConvertAndProcessor(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		//使用此方法为消息增加消息属性
		MessagePostProcessor processor = new MessagePostProcessor() {
			public Message postProcessMessage(Message message) throws JMSException {
				message.setJMSPriority(8);
				return message;
			}
		};
		jmsTemplate.convertAndSend((Object)"This is convert and processor send",processor);
	}
	
	/**
	 * 自动转换发送非默认消息队列
	 */
	@Test
	public void testConvertAndSendNotDefault(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		Destination destination = (Destination)context.getBean("queueDestination");
		MessagePostProcessor postProcessor = new MessagePostProcessor() {
			
			public Message postProcessMessage(Message message) throws JMSException {
				message.setJMSPriority(8);
				return message;
			}
		};
		jmsTemplate.convertAndSend(destination, "This is not default convert and send", postProcessor);
	}
}
