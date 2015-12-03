package com.kris.chat.view;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import com.kris.chat.connection.ConnectionManager;
import com.kris.chat.connection.MessageListenerAdapter;
import com.kris.chat.model.ChatClientModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MainLayoutController implements Initializable {
	private ConnectionManager connectionManager;
	private ObservableList<String> chatrooms;
	private ObservableList<String> observableUsers;
	private PresentationController presentationController;
	
	@FXML
	private Label roomName;

	@FXML
	private ListView<String> chatRoomList = new ListView<>();
	
	@FXML
	private ListView<String> usersList = new ListView<>();

	@FXML
	private TextField inputField;

	@FXML
	private TitledPane roomsPane;

	@FXML
	private TitledPane usersPane;

	@FXML
	private Accordion menu;

	@FXML
	private TextArea outputField;
	
	private ChatClientModel model;
	
	public Label getRoomName() {
		return roomName;
	}

	public void setRoomName(Label roomName) {
		this.roomName = roomName;
	}

	public TitledPane getRoomsPane() {
		return roomsPane;
	}

	public void setRoomsPane(TitledPane roomsPane) {
		this.roomsPane = roomsPane;
	}

	public TitledPane getUsersPane() {
		return usersPane;
	}

	public void setUsersPane(TitledPane usersPane) {
		this.usersPane = usersPane;
	}

	public Accordion getMenu() {
		return menu;
	}

	public void setMenu(Accordion menu) {
		this.menu = menu;
	}

	public void setModel(ChatClientModel model) {
		this.model = model;
	}
	
	public void setConnecitonManager(ConnectionManager connecitonManager) {
		if (this.connectionManager != null) {
			this.connectionManager.removeMessageListener(adapter);
		}
		this.connectionManager = connecitonManager;
		this.connectionManager.addMessageListener(adapter);
	}
	
	public void setPresentationController(PresentationController presentationController) {
		this.presentationController = presentationController;
	}

	private MessageListenerAdapter adapter = new MessageListenerAdapter() {
		@Override
		public void handleChatMessage(String username, String message) {
			printMessageToChat(username, message);
		}
		
		@Override
		public void handleReceiveRoomsList(List<String> chatrooms) {
			receiveChatrooms(chatrooms);
		}
		
		@Override
		public void handleReceiveUsersList(List<String> users) {
			receiveUsers(users);
		}

	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menu.setExpandedPane(roomsPane);
		chatrooms = FXCollections.observableArrayList();
		observableUsers = FXCollections.observableArrayList();
	}

	@FXML
	private void handleLogOut(ActionEvent event) throws IOException {
		connectionManager.handleExit(model.getClientUser().getUsername());
		connectionManager.getMessageListeners().clear();
		connectionManager.getSocket().close();
		presentationController.showLoginLayout();
	}

	@FXML
	private void handleCreateButton(ActionEvent event) {
		presentationController.showDialogLayout(event, this);
	}

	@FXML
	private void handleSendButton() {
		if (inputField.getLength() < 1) {
			return;
		}
		String message = inputField.getText();
		printMessageToChat(model.getClientUser().getUsername(),message);
		inputField.setText("");
		connectionManager.sendChatMessage(model.getClientUser().getUsername(), message);
	}

	@FXML
	private void handleRoomSelect() {
		if (!chatrooms.isEmpty() && chatRoomList.getSelectionModel().getSelectedItem().toString() != null) {
			roomName.setText("Chatroom: " + chatRoomList.getSelectionModel().getSelectedItem().toString());
			connectionManager.joinRoom(model.getClientUser().getUsername(), chatRoomList.getSelectionModel().getSelectedItem().toString());
			roomsPane.setExpanded(false);
			usersPane.setExpanded(true);
		}
	}

	@FXML
	private void sendTextOnEnterPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			handleSendButton();
		}
	}
	

	private void printMessageToChat(String username, String message) {
		outputField.appendText(username + ":" + message + "\n");
	}
	
	private void receiveChatrooms(List<String> chatroomsList) {
		chatrooms.clear();
		chatrooms.addAll(chatroomsList);
		assingChatRooms();
	}
	
	private void receiveUsers(List<String> users) {
		observableUsers.clear();
		observableUsers.addAll(users);
		assingUsersList();
	}
	
	private void assingChatRooms() {
		chatRoomList.setItems(chatrooms);
		chatRoomList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	}
	
	private void assingUsersList() {
		usersList.setItems(observableUsers);
		usersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	}
}
