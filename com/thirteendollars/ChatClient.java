package com.thirteendollars;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public abstract class ChatClient {
	
	private final int READ_BUFFER_SIZE_BYTES=1024;
	private Socket mSocket;
	private InputStream mInputSream;
	private OutputStream mOutputStream;
	private String mUsername="A";
	private boolean mConnection;
	
	// Constructor for server-side chat version
	public ChatClient(Socket socket) throws IOException {
		mInputSream = socket.getInputStream();
		mOutputStream = socket.getOutputStream();
		mSocket=socket;
		new ReadThread().start();
		mConnection=true;
	}
	
	// Constructor for client-side chat version
	public ChatClient(String username,String serverAddress,int serverPort) throws IOException {
		mSocket = new Socket(serverAddress,serverPort);
		mInputSream = mSocket.getInputStream();
		mOutputStream = mSocket.getOutputStream();
		sendMessage(new ChatMessage( ChatMessage.TYPE_STATUS,username,ChatMessage.CONTENT_ONLINE ));
		mUsername=username;
		new ReadThread().start();
		mConnection=true;
	}
	
	
	
	public void closeConnection() {
		mConnection=false;
		try{
			mInputSream.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			mOutputStream.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			mSocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendMessage(ChatMessage mssg) {
		new SendThread(mssg).start();
	}
	
	
	public boolean isConnected(){
		return mConnection;
	}
	
	public String getUsername(){
		return mUsername;
	}
	
	public void setUsername(String username){
		mUsername=username;
	}
	
	
	public abstract void onReceived(ChatMessage mssg);
	public abstract void onOfflineStatus();
	
	
	private class SendThread extends Thread {
		private ChatMessage mMessage;
		
		public SendThread(ChatMessage message){
			mMessage=message;
		}
		
		@Override
		public void run() {
			try {
				mOutputStream.write( mMessage.getBindedMessage().getBytes() );				
			} catch (IOException e) {
				onOfflineStatus();
				mConnection=false;
				e.printStackTrace();
			}
		}
	}
	
	private class ReadThread extends Thread {
		
		@Override
		public void run() {
	
			//wait for messages
			while ( !isInterrupted() ) {
				byte[] buffer = new byte[READ_BUFFER_SIZE_BYTES];
				try {
					int status = mInputSream.read(buffer);
					if(  status>0 ){
							onReceived( new ChatMessage(new String(buffer)) );
					}
					else if(status==-1){
						onOfflineStatus();
						mConnection=false;
						break;
					}
				} catch (IOException e) {
					onOfflineStatus();
					e.printStackTrace();
					mConnection=false;
					break;
				}
			}
		}
	}
	

}
