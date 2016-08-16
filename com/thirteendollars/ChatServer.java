package com.thirteendollars;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class ChatServer {

	public static final int MAX_CLIENTS_NUMBER = 10;
	private ServerSocket mServerSocket;
	private ArrayList<ChatClient> mClientsList;
	private ListenThread mListenThread;

	public ChatServer(int port) throws IOException {
		mServerSocket = new ServerSocket(port);
		mClientsList = new ArrayList<ChatClient>(MAX_CLIENTS_NUMBER);
		mListenThread = new ListenThread();
		mListenThread.start();
		System.out.println("Server started on "
				+ mServerSocket.getLocalSocketAddress().toString());
	}

	public abstract void onUserOnline(String username, boolean isOnline);

	public abstract void onServerReceived(ChatMessage mssg);

	public void stop() {
		// stop listenning
		mListenThread.interrupt();
		try {
			mServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// close all clients sockets
		for (ChatClient client : mClientsList) {
			client.closeConnection();
		}

	}

	public void broadcastMessage(ChatMessage message) {

		for (ChatClient client : mClientsList) {
			client.sendMessage(message);
		}

	}

	private class ListenThread extends Thread {

		@Override
		public void run() {

			while (!isInterrupted()) {
				Socket socket;
				try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}

				if (mClientsList.size() < MAX_CLIENTS_NUMBER) {
					try {
						addClient(socket);
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
				} else {
					// reject connection
					try {
						OutputStream out;
						out = socket.getOutputStream();
						out.write(new ChatMessage(ChatMessage.TYPE_MESSAGE,
								ChatMessage.SENDER_SERVER,
								"Client limit reached. Try later")
								.getBindedMessage().getBytes());
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private void addClient(Socket socket) throws IOException {

			ChatClient client = new ChatClient(socket) {

				@Override
				public void onOfflineStatus() {
					mClientsList.remove(this);
					this.closeConnection();
					ChatMessage mssg = new ChatMessage(ChatMessage.TYPE_STATUS,
							this.getUsername(), ChatMessage.CONTENT_OFFLINE);
					broadcastMessage(mssg);
					onUserOnline(this.getUsername(), false);
				}

				@Override
				public void onReceived(ChatMessage mssg) {
					if (mssg.getType() == ChatMessage.TYPE_STATUS) {
						boolean isUserOnline = mssg.getContent().equals(
								ChatMessage.CONTENT_ONLINE);
						onUserOnline(mssg.getSender(), isUserOnline);
						if (isUserOnline) {
							this.setUsername(mssg.getSender());
							String[] users = new String[mClientsList.size()];
							int i = 0;
							for (ChatClient userName : mClientsList) {
								users[i] = userName.getUsername();
								i++;
							}
							this.sendMessage(new ChatMessage(users));
						}
					} else {
						onServerReceived(mssg);
					}
					broadcastMessage(mssg);
				}
			};
			mClientsList.add(client);
		}

	}

}
