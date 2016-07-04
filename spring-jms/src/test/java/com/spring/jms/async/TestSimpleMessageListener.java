package com.spring.jms.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSimpleMessageListener {
	
	/**
	 * 测试实现MessageListener接口
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	@Test
	public void test() throws IOException{
		new ClassPathXmlApplicationContext("classpath:applicationContext-jms-async.xml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		reader.readLine();
		System.exit(1);
	}
}
