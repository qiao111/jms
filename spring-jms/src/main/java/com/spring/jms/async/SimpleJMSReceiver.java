package com.spring.jms.async;

import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class SimpleJMSReceiver {
	//对于默认情况下，需要定义几种转换的类型 String byte[] Map Object
	public void handleMessage(String message){
		System.out.println("消息转换为java对象下接收的消息:" + message);
	}
	
	public void handleMessage(byte[] message){
		System.out.println("消息转换为java对象下接收的消息:" + new String(message));
	}
	
	public void handleMessage(Map message){
		System.out.println("接收MapMessage类型的消息");
	}
	
	public void handleMessage(Object message){
		System.out.println("接收ObjectMessage类型的消息");
	}
	//取消消息转换 此时可以访问消息中的消息头属性
	public void handleMessage(TextMessage message){
		try {
			System.out.println(message.getText());
			System.out.println(message.getJMSMessageID());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void handleMessage(BytesMessage message){
		try {
			System.out.println(message.getJMSMessageID());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	public void handleMessage(MapMessage message){
		try {
			System.out.println(message.getJMSMessageID());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	public void handleMessage(ObjectMessage message){
		try {
			System.out.println(message.getJMSMessageID());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void handleMessage(StreamMessage message){
		try {
			System.out.println(message.getJMSMessageID());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置默认处理方法
	 * @param message
	 */
	public void handleDefaultMethod(TextMessage message){
		try {
			System.out.println("更改默认方法：" + message.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
