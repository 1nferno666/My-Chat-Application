package com.kris.chat.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Chatroom {
	private Map<User, Socket> socketToUserMap;
	private final String name;

	public Chatroom(String name) {
		this.name = name;
		socketToUserMap = new HashMap<>();
	}

	public Map<User, Socket> getSocketToUserMap() {
		return socketToUserMap;
	}

	public String getName() {
		return name;
	}
	
	public List<String> getUsers() {
		List<String> users = new ArrayList<>();
		for (Map.Entry<User, Socket> entry: socketToUserMap.entrySet()) {
			users.add(entry.getKey().getUsername());
		}
		return users;
	}
}
