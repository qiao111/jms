package com.chat;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

/**
 * 聊天室
 * @author qiaolin
 *
 */
public class Chat implements MessageListener{
	
	private TopicSession pubSession;
	
	private TopicPublisher publisher;
	
	private TopicConnection connection;
	
	private String username;
	
	
	public void onMessage(Message arg0) {
		
	}

}
