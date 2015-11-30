package com.kris.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.kris.chat.connection.MessageSerialization;
import com.kris.chat.messages.ClientServerMessages;
import com.kris.chat.model.Chatroom;
import com.kris.chat.model.User;

public class ChatService {
	private Map<User, String> userToChatroomNameMap;
	private List<User> usersList;
	private List<Chatroom> chatroomsList;
	private Set<Socket> allSockets;

	public ChatService() {
		usersList = new ArrayList<>();
		chatroomsList = new ArrayList<>();
		userToChatroomNameMap = new ConcurrentHashMap<>();
		allSockets = new HashSet<>();
	}
	
	private User stringToUser(String senderUsername) {
		for (User user : usersList) {
			if (user.getUsername().equals(senderUsername)) {
				return user;
			}
		}
		return null;
	}

	private Chatroom stringToChatroom(String chatroomName) {
		for (Chatroom chatroom : chatroomsList) {
			if (chatroom.getName().equals(chatroomName)) {
				return chatroom;
			}
		}
		return null;
	}
	
	private void removeUserFromChatroom(String username) {
		for (Chatroom room : chatroomsList) {
			if (room.equals(stringToChatroom(userToChatroomNameMap.get(username)))) {
				stringToChatroom(userToChatroomNameMap.get(username)).getSocketToUserMap().remove(username);
			}
		}
	}
	
	synchronized public boolean authenticate(Socket sender, String username, String password) {
		User newUser = new User(username, password);
		for (User user : usersList) {
			if (user.getUsername().equals(newUser.getUsername())) {
				return false;
			}
		}
		usersList.add(newUser);
		allSockets.add(sender);
		return true;
	}

	synchronized public void sendMessage(String senderUsername, String message) {
		if (stringToChatroom(userToChatroomNameMap.get(stringToUser(senderUsername))) != null) {
			Chatroom tempChatoom = stringToChatroom(userToChatroomNameMap.get(stringToUser(senderUsername)));
			
			for (Map.Entry<User, Socket> entry : tempChatoom.getSocketToUserMap().entrySet()) {
				if (!entry.getKey().getUsername().equals(senderUsername)) {
					String outputMessage = MessageSerialization.createMessage(ClientServerMessages.CHAT_MESSAGE,
							senderUsername, message);
					try {
						PrintWriter bufferedWriter = new PrintWriter(entry.getValue().getOutputStream());
						bufferedWriter.println(outputMessage);
						bufferedWriter.flush();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	synchronized public void createRoom(Socket sender, String senderUsername, String chatroomName) throws NullPointerException {
		removeUserFromChatroom(senderUsername);
		Chatroom newChatRoom = new Chatroom(chatroomName);
		
		for (Chatroom chatroom: chatroomsList) {
			if (newChatRoom.getName().equals(chatroom.getName())) {
				return;
			}
		}
		newChatRoom.getSocketToUserMap().put(stringToUser(senderUsername), sender);
		userToChatroomNameMap.put(stringToUser(senderUsername), chatroomName);
		chatroomsList.add(newChatRoom);
		updateChatrooms();
	}

	synchronized public void joinRoom(Socket sender, String senderUsername, String chatroomName) throws NullPointerException {
		removeUserFromChatroom(senderUsername);
		for (int i = 0; i < chatroomsList.size(); i++) {
			if (chatroomsList.get(i).getName().equals(chatroomName)) {
				chatroomsList.get(i).getSocketToUserMap().put(stringToUser(senderUsername), sender);
			}
		}
		userToChatroomNameMap.put(stringToUser(senderUsername), stringToChatroom(chatroomName).getName());
	}

	public void updateChatrooms() {
		String outputMessage = MessageSerialization.createMessage(ClientServerMessages.CHATROOMS_LIST);
		for (Chatroom chatroom: chatroomsList) {
			outputMessage += " " + MessageSerialization.createMessage(chatroom.getName());
		}
		for (Socket user: allSockets) {
			try {
				PrintWriter bufferedWriter = new PrintWriter(user.getOutputStream());
				bufferedWriter.println(outputMessage);
				bufferedWriter.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	synchronized public void removeUser(Socket sender, String senderUsername) {
		removeUserFromChatroom(senderUsername);
		userToChatroomNameMap.remove(stringToUser(senderUsername));
		Iterator<User> itr = usersList.iterator();
		while (itr.hasNext()) {
			String check = itr.next().getUsername();
			if (check.equals(senderUsername)) {
				itr.remove();
				break;
			}
		}
		System.out.println(senderUsername + " left!");
		allSockets.remove(sender);
	}
	
	synchronized public void updateUsers(String chatroomName) {
		String outputMessage = MessageSerialization.createMessage(ClientServerMessages.USERS_LIST);
		for (Chatroom chatroom : chatroomsList) {
			if (chatroom.getName().equals(chatroomName)) {
				List<String> userlist = chatroom.getUsers();
				for (String string : userlist) {
					outputMessage += " " + MessageSerialization.createMessage(string);
				}
				
				for (Map.Entry<User, Socket> entry: chatroom.getSocketToUserMap().entrySet()) {
					PrintWriter bufferedWriter;
					try {
						bufferedWriter = new PrintWriter(entry.getValue().getOutputStream());
						System.out.println("updateUsers" + outputMessage);
						bufferedWriter.println(outputMessage);
						bufferedWriter.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
