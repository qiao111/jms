package com.spring.jms.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class TestMessageListenerAdapter {
	
	@Test
	public void testSend(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-jms-sync.xml");
		JmsTemplate jmsTemplate = (JmsTemplate)context.getBean("jmsTemplate");
		jmsTemplate.convertAndSend("发送消息测试");
	}
	
	/**
	 * 测试MessageListenerAdapter
	 * @throws IOException 
	 */
	@Test
	public void testMessageAdapter() throws IOException{
		new ClassPathXmlApplicationContext("classpath:applicationContext-jms-async.xml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		reader.readLine();
		System.exit(1);
	}
}
