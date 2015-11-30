package com.kris.chat.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import static com.kris.chat.messages.ClientServerMessages.*;

public class ConnectionManager {
	private  Socket socket;
	private static Set<MessageListener> messageListeners;
	private BufferedReader inboundChannel;
	private PrintWriter printWriter;

	public ConnectionManager() {
		messageListeners = new HashSet<>();
	}
	
	public void setMessageListeners(Set<MessageListener> messageListeners) {
		ConnectionManager.messageListeners = messageListeners;
	}
	
	public Set<MessageListener> getMessageListeners() {
		return messageListeners;
	}

	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void authenticate(String username, String password) {
		sendMessage(MessageSerialization.createMessage(AUTHENTICATE, username, password));
	}
	
	public void registerUser(String username, String password) {
		sendMessage(MessageSerialization.createMessage(NEW_USER, username, password));
	}

	public void sendChatMessage(String username, String message) {
		sendMessage(MessageSerialization.createMessage(CHAT_MESSAGE, username, message));
	}
	
	public void createRoom(String senderUsername, String chatroomName) {
		sendMessage(MessageSerialization.createMessage(CREATE_ROOM, senderUsername, chatroomName));
		sendMessage(MessageSerialization.createMessage(USERS_LIST, chatroomName));
	}
	
	public void joinRoom(String senderUsername, String chatroomName) {
		sendMessage(MessageSerialization.createMessage(JOIN_ROOM, senderUsername, chatroomName));
		sendMessage(MessageSerialization.createMessage(USERS_LIST, chatroomName));
	}
	
	public void handleExit(String senderUsername) {
		sendMessage(MessageSerialization.createMessage(REMOVE_USER, senderUsername));
	}
	
	public void requestInformation() {
		sendMessage(MessageSerialization.createMessage(GET_INFO));
	}
	
	private void sendMessage(String message) {
		printWriter.println(message);
		printWriter.flush();
	}
	
	synchronized public void establishConnection() throws IOException, UnknownHostException{
		socket = new Socket("localhost", 1192);
		inboundChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		printWriter = new PrintWriter(socket.getOutputStream());

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!socket.isClosed()) {
					try {
						String message = inboundChannel.readLine();
						for (MessageListener messageListener: messageListeners){
							messageListener.dispatchMessage(message);
						}

					} catch (IOException e) {
						//Handle Errors
					}
				}
				setSocket(null);
			}
		});

		thread.start();
	}

	synchronized public boolean isConnectionEstablished() {
		return socket != null && socket.isConnected();
	}


	public void removeMessageListener(MessageListener messageListener) {
		messageListeners.remove(messageListener);
	}

	public void addMessageListener(MessageListener messageListener) {
		messageListeners.add(messageListener);
	}
}
