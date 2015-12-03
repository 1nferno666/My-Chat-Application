package com.kris.chat.connection;

import static com.kris.chat.messages.ClientServerMessages.*;

import java.util.ArrayList;
import java.util.List;

import com.kris.chat.interfaces.MessageListener;

import javafx.application.Platform;

public class MessageListenerAdapter implements MessageListener {

	@Override
	public void dispatchMessage(String dispatchMessage) {
		Platform.runLater(() -> handleServerMessage(dispatchMessage));
	}

	private void handleServerMessage(String message) {
		if (message == null) {
			return;
		}
		System.out.println();
		List<String> messageTokens = MessageSerialization.splitMessage(message);
		String command = messageTokens.get(0);
		switch (command) {
			case (LOG_IN):
				System.out.println("log in");
				handleLogin(Boolean.parseBoolean(messageTokens.get(1)));
				break;
			case (CHAT_MESSAGE):
				handleChatMessage(messageTokens.get(1), messageTokens.get(2));
				break;
			case (CHATROOMS_LIST):
				List<String> chatrooms = new ArrayList<>();
				System.out.println("Size" + messageTokens.size());
				
				for (int i = 1; i < messageTokens.size(); i++) {
						chatrooms.add(messageTokens.get(i));
				}
				
				handleReceiveRoomsList(chatrooms);
				break;
				
			case (USERS_LIST):
				List<String> userlist = new ArrayList<>();
				System.out.println("Size" + messageTokens.size());
			
				for (int i = 1; i < messageTokens.size(); i++) {
					System.out.println(messageTokens.get(i));
					userlist.add(messageTokens.get(i));
			}
				handleReceiveUsersList(userlist);
				break;
			}
	}

	public void handleChatMessage(String username, String message) {

	}

	public void handleLogin(boolean status) {

	}
	
	public void handleReceiveRoomsList(List<String> chatrooms) {
		
	}
	
	public void handleReceiveUsersList(List<String> users) {
		
	}
}
