package com.p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

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
 * 负责向包含工资额和贷款额的一个队列发送LoanRequest消息
 * @author qiaolin
 *
 */
public class QBorrower {
	
	private QueueConnection qConnection;
	
	private QueueSession qSession;
	
	private Queue responseQ ;
	
	private Queue requestQ;
	
	public QBorrower(String queueFactory,String requestQueue,String responseQueue){
		try {
			//获取连接提供者 并获取连接
			Context context = new InitialContext();
			QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup(queueFactory);
			qConnection = queueConnectionFactory.createQueueConnection();
			//创建JMS会话
			qSession = qConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			//获取请求队列
			requestQ = (Queue) context.lookup(requestQueue);
			//获取响应队列
			responseQ = (Queue) context.lookup(responseQueue);
			//启动连接
			qConnection.start();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送贷款请求
	 * @param salary
	 * @param loanAmt
	 */
	private void sendLoanRequest(double salary,double loanAmt){
		try {
			MapMessage message = qSession.createMapMessage();
			message.setDouble("Salary", salary);
			message.setDouble("LoanAmount", loanAmt);
			message.setJMSReplyTo(responseQ);//设置应答队列 贷款响应队列
			//创建发送者 发送到请求贷款队列
			QueueSender sender = qSession.createSender(requestQ);
			sender.send(message);
			System.out.println("发送消息");
			//设置过滤器 获取当前的消息 此处的过滤器一定要写正确 否则无法接受到响应信息
			String filter = "JMSCorrelationID='" + message.getJMSMessageID() + "'";
			System.out.println(filter);
			System.out.println(responseQ.getQueueName());
			//创建接收者  等待响应队列的响应信息
			QueueReceiver receiver = qSession.createReceiver(responseQ,filter);
			//接受响应消息 此处表示不设置过期时间 如果需要设置则receiver.receive(3000) 以毫秒为单位
			TextMessage textMessage = (TextMessage) receiver.receive();
			if(textMessage == null){
				System.out.println("QLender not responding");
			}else{
				System.out.println("Loan request was " + textMessage.getText());
			}
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
			qConnection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	public static void main(String[] args) {
		String queueFactory = "",requestQueue = "",responseQueue = "";
		if(args.length != 3){
			System.out.println("参数不正确");
			System.exit(0);
		}
		queueFactory = args[0];
		requestQueue = args[1];
		responseQueue = args[2];
		QBorrower borrower = new QBorrower(queueFactory, requestQueue, responseQueue);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				String loanQuest = reader.readLine();
				if(loanQuest == null || loanQuest.trim().length() < 0){
					borrower.exit();
				}
				StringTokenizer tokenizer = new StringTokenizer(loanQuest, ",");
				double salary = Double.valueOf(tokenizer.nextToken().trim());
				double loanAmt = Double.valueOf(tokenizer.nextToken().trim());
				borrower.sendLoanRequest(salary, loanAmt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
