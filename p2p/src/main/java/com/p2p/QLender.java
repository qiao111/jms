package com.p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * 监听贷款申请队列并响应
 * @author qiaolin
 *
 */
public class QLender implements MessageListener{
	
	private QueueConnection qConnect;
	
	private QueueSession qSession;
	
	private Queue requestQ ;
	
	public QLender(String queueConnectionFactory,String requestQueue){
		try {
			//连接到提供者 并获取连接信息
			Context context = new InitialContext();
			QueueConnectionFactory qConnectionFactory = (QueueConnectionFactory) context.lookup(queueConnectionFactory);
			qConnect = qConnectionFactory.createQueueConnection();
			//创建JMS会话
			qSession = qConnect.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			//获取请求队列
			requestQ = (Queue) context.lookup(requestQueue);
			//启动连接
			qConnect.start();
			//创建接收者 主要接受贷款申请队列的消息
			QueueReceiver receiver = qSession.createReceiver(requestQ);
			receiver.setMessageListener(this);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 消息监听
	 */
	public void onMessage(Message message) {
		try {
			boolean accepted = false;
			MapMessage map = (MapMessage) message;
			double salary = map.getDouble("Salary");
			double loanAmt = map.getDouble("LoanAmount");
			if(loanAmt < 200000){
				accepted = (salary/loanAmt) > 0.25;
			}else{
				accepted = (salary/loanAmt) > 0.33;
			}
			System.out.println("loan is " + (accepted?"Accepted":"Declined") );
			//设置响应消息
			TextMessage responseMessage = qSession.createTextMessage();
			responseMessage.setText(accepted?"Accepted":"Declined");
			//设置消息关联
			responseMessage.setJMSCorrelationID(map.getJMSMessageID());
			//创建发送者
			QueueSender sender = qSession.createSender((Queue) message.getJMSReplyTo());
			sender.send(responseMessage);
			System.out.println("load response is send ");
		} catch (JMSException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 退出操作
	 */
	private void exit(){
		try {
			qConnect.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	public static void main(String[] args) {
		String queueFactory = "",requestQueue = "";
		if(args.length != 2){
			System.out.println("参数不正确");
			System.exit(0);
		}
		queueFactory = args[0];
		requestQueue = args[1];
		QLender lender = new QLender(queueFactory, requestQueue);
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			reader.readLine();
			lender.exit();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
