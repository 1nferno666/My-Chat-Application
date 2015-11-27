package com.kris.chat.view;

import java.net.URL;
import java.util.ResourceBundle;

import com.kris.chat.connection.ConnectionManager;
import com.kris.chat.model.ChatClientModel;
import com.kris.chat.model.Chatroom;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateRoomDialogController implements Initializable{
	private ConnectionManager connectionManager;
	private ChatClientModel model;
	private MainLayoutController main;
	
	public void setMainLayoutController(MainLayoutController main) {
		this.main = main;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
	
	public void setModel(ChatClientModel model) {
		this.model = model;
	}

	@FXML
	TextField chatroomName;
	
	@FXML
	private void handleCancel(ActionEvent event) {
		closeCurrentStage(event);
	}
	
	@FXML
	private void handleCreate(ActionEvent event) {
		connectionManager.createRoom(model.getClientUser().getUsername(), chatroomName.getText());
		main.getRoomName().setText("Chatroom: " + chatroomName.getText());
		main.getRoomsPane().setExpanded(false);
		main.getUsersPane().setExpanded(true);
		closeCurrentStage(event);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	private void closeCurrentStage(ActionEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}
	
}
