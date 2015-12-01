package com.kris.server;

import static com.kris.chat.messages.ClientServerMessages.*;
import static com.kris.chat.messages.ClientServerMessages.LOG_IN;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import com.kris.chat.connection.MessageSerialization;

public class ChatServerThread implements Runnable {
	private Socket socket;
	private Scanner input;
	private PrintWriter output;
	private ChatService chatService;

	public ChatServerThread(Socket socket, ChatService chatService) {
		this.socket = socket;
		this.chatService = chatService;
	}

	@Override
	public void run() {
		try {
			input = new Scanner(socket.getInputStream());
			output = new PrintWriter(socket.getOutputStream());
			String senderUsername;
			String chatroomName;
			
			while (socket.isConnected()) {
				if (!input.hasNextLine()) {
					break;
				}
				String message = input.nextLine();
				ConsoleMessage.print("Server message: " + message);
				List<String> tokens = MessageSerialization.splitMessage(message);
				String command = tokens.get(0);
				switch (command) {
				case AUTHENTICATE:
					String username = tokens.get(1);
					String password = tokens.get(2);
					Boolean isValidUser = chatService.authenticate(socket, username, password);
					String outputMessage = MessageSerialization.createMessage(LOG_IN, isValidUser.toString());
					output.println(outputMessage);
					output.flush();
					break;
				case NEW_USER:
					// TODO
					break;
				case CHAT_MESSAGE: 
					senderUsername = tokens.get(1);
					if (tokens.get(2) == null) {
						break;
					}
					String chatMessage = tokens.get(2);
					chatService.sendMessage(senderUsername, chatMessage);
					break;
				case CREATE_ROOM:
					senderUsername = tokens.get(1);
					System.out.println("Room created by " + senderUsername);
					chatroomName = tokens.get(2);
					chatService.createRoom(socket, senderUsername, chatroomName);
					break;
				case JOIN_ROOM:
					senderUsername = tokens.get(1);
					chatroomName = tokens.get(2);
					chatService.joinRoom(socket, senderUsername, chatroomName);
					break;
				
				case REMOVE_USER:
					senderUsername = tokens.get(1);
					chatService.removeUser(socket, senderUsername);
					break;
				
				case USERS_LIST:
					chatroomName = tokens.get(1);
					chatService.updateUsers(chatroomName);
					break;
					
				case GET_INFO:
					chatService.updateChatrooms();
					break;
				}
			}

		} catch (IOException e) {;
			e.printStackTrace();
		} 
	}
}
