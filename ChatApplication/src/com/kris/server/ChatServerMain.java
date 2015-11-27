package com.kris.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServerMain {


	public static void main(String[] args) {
		final int PORT = 1192;
		ChatService chatService = new ChatService();

		try (ServerSocket serverSocket = new ServerSocket(PORT)){
			System.out.println("Waiting for clients...");

			while (true) {
				Socket newClientScocket = serverSocket.accept();
				System.out.println("Client connected from: " + newClientScocket.getLocalAddress().getHostName());

				ChatServerThread chatServerThead = new ChatServerThread(newClientScocket, chatService);
				Thread startThread = new Thread(chatServerThead);
				startThread.start();
			}

		} catch (IOException e) {
			System.out.println(e);
		}

	}

}
