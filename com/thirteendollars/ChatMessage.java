package com.thirteendollars;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ChatMessage {
	
	
	private String mContent="";
	private String mSender="";
	private int mType;
	
	
	private String mBindedMessage="";
	
	public static final int TYPE_MESSAGE=1;
	public static final int TYPE_STATUS=2;
	
	public static final String SENDER_SERVER="SERVER";
	public static final String CONTENT_ONLINE="online";
	public static final String CONTENT_OFFLINE="offline";
	
	private final String DELIMETER="{|.|}";
	
	public ChatMessage(int TYPE,String sender,String content){
		mType=TYPE;
		mSender=sender.trim();
		mContent=content.trim();
		mBindedMessage=TYPE+DELIMETER+mSender+DELIMETER+mContent;
	}
	
	
	public ChatMessage(String[] usersList){
		mType=TYPE_STATUS;
		mSender=SENDER_SERVER;
		for(String user : usersList){
			mContent+=user.trim()+DELIMETER;
		}
		mBindedMessage=mType+DELIMETER+mSender+DELIMETER+mContent;
	}

	
	public ChatMessage(String bindedMessage) {
		String[] tokens = bindedMessage.split(Pattern.quote( DELIMETER) );
		mType=Integer.parseInt( tokens[0].trim() );
		mSender=tokens[1].trim();
		mContent=tokens[2].trim();
		mBindedMessage=bindedMessage.trim();
	}


	public String getContent() {
		return mContent;
	}


	public String getSender() {
		return mSender;
	}


	public int getType() {
		return mType;
	}


	public String getBindedMessage() {
		return mBindedMessage;
	}
	
	public String[] getUsersList(){
		String[] users = mBindedMessage.split(Pattern.quote( DELIMETER) );
		return Arrays.copyOfRange(users, 2, users.length);
	}
	

	
}
