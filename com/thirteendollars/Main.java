
package com.thirteendollars;

import java.io.IOException;




public class Main {
	
	
	private final int MODE_DISCONNECTED=-1;
	private final int MODE_CLIENT=-2;
	private final int MODE_SERVER=-3;
	private GUI mGUI;
	private int mCurrentMode= MODE_DISCONNECTED;
	private ChatServer mServer;
	private ChatClient mClient;
	
	private void closeAllConnections(){
		if( mClient!=null ){
			mClient.closeConnection();
			mClient=null;
		}
		if( mServer!=null ){
			mServer.stop();
			mServer=null;
		}
		mGUI.clearUsersOnlineStatus();
		mGUI.clearChatWindow();
	}
	public Main(){
	    
    	mGUI = new GUI() {
    		
    		@Override
    		public void onWindowClosed() {
    			closeAllConnections();		
    		}
			
			@Override
			public void onSend(String message) {
				if( mCurrentMode==MODE_CLIENT ){
					mClient.sendMessage( new ChatMessage(ChatMessage.TYPE_MESSAGE, mClient.getUsername(), message) );
				}
				else if( mCurrentMode==MODE_SERVER ){
					mServer.broadcastMessage(new ChatMessage(ChatMessage.TYPE_MESSAGE, ChatMessage.SENDER_SERVER, message) );
					mGUI.updateChatTextWindow(ChatMessage.SENDER_SERVER+": "+message );
				}
				
			}
			
			@Override
			public void onConnectAsServer(int portNumber) {
				if(mCurrentMode!=MODE_DISCONNECTED){
					return;
				}
    			closeAllConnections();	
				
				try {
						mServer= new ChatServer( portNumber ) {
							
							@Override
							public void onUserOnline(String user,boolean isOnline) {
								mGUI.changeUserOnlineStatus(user, isOnline);
							}
							
							@Override
							public void onServerReceived(ChatMessage message) {
								mGUI.updateChatTextWindow(message.getSender()+": "+message.getContent() );
							}
						};
						mCurrentMode=MODE_SERVER;
						mGUI.updateChatTextWindow("You are online" );
				} catch (IOException e) {
					e.printStackTrace();
					mCurrentMode=MODE_DISCONNECTED;
					mGUI.updateChatTextWindow("Connecting error: "+e.getMessage() );
				}
			}
			
			@Override
			public void onConnectAsClient(String username, String ipAddress, int portNumber) {
				if(mCurrentMode!=MODE_DISCONNECTED){
					return;
				}
    			closeAllConnections();	
				
				try {
						
						mClient = new ChatClient (username,ipAddress,portNumber ) {
						
							@Override
							public void onReceived(ChatMessage message) {
								if(message.getType()==ChatMessage.TYPE_STATUS){
									if( message.getSender().equals( ChatMessage.SENDER_SERVER ) ){// it will be users online list
										for( String user : message.getUsersList() ){
											if(this.getUsername().equals(user) ){
												continue;
											}
											changeUserOnlineStatus(user.trim(),true);
										}
									}
									else{ // otherwise one user status change
										String username=message.getSender();
										boolean isOnline= message.getContent().equals( ChatMessage.CONTENT_ONLINE );
										mGUI.changeUserOnlineStatus(username, isOnline);
									}
								}
								else{
									mGUI.updateChatTextWindow(message.getSender()+": "+message.getContent() );
								}
								
							}
							
							@Override
							public void onOfflineStatus() {
								mGUI.updateChatTextWindow("Server gone offline");
								this.closeConnection();
							}
	
						};
						mCurrentMode=MODE_CLIENT;
				}
				catch(IOException ex){
					ex.printStackTrace();
					mCurrentMode=MODE_DISCONNECTED;
					mGUI.updateChatTextWindow("Connecting error: "+ex.getMessage() );
				}
			}
		};
		mGUI.setVisible(true);
	}
    
    public static void main( String[] args){
    		new Main();
    }
	
	


}
