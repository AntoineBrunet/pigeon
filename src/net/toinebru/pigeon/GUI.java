package net.toinebru.pigeon;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.Map;
import java.util.List;
import java.net.InetAddress;

public class GUI extends Application {
	private Pigeon pigeon;
	private List<Node> boardElems;

	public void receive(Message m) {
		Platform.runLater(() -> {
			this.boardElems.add(new TextFlow(new Text(m.toString())));
		});
	}

	@Override
	public void init() throws Exception {
		List<String> unnamed = getParameters().getUnnamed();
		Map<String,String> named = getParameters().getNamed();
		String defaultPort = Integer.toString(Pigeon.DEFAULT_PORT);
		int lp = Integer.parseInt(named.getOrDefault("local", defaultPort));
		int dp = Integer.parseInt(named.getOrDefault("dist" , defaultPort));
		String host = "localhost";
		if (unnamed.size() > 1) {
			host = unnamed.get(1);
		} 
		this.pigeon = new Pigeon(InetAddress.getByName(host), this::receive, lp, dp);
	}

	@Override
	public void start(Stage primaryStage) {
		GridPane grid = new GridPane();
		grid.setHgap(2);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));

		//Defining the Message text field
		final TextField message = new TextField();
		message.setPromptText("Enter your message");
		GridPane.setConstraints(message, 0, 1);
		GridPane.setHgrow(message, Priority.ALWAYS);
		grid.getChildren().add(message);
		
		//Defining the Submit button
		Button submit = new Button("Submit");
		GridPane.setConstraints(submit, 1, 1);
		grid.getChildren().add(submit);

		EventHandler<ActionEvent> sendMsg = (ActionEvent e) -> {
			String msg = message.getText();
			if (msg != null && !msg.isEmpty()) {
				Message mess = new Message(msg);
				pigeon.send(mess);
				receive(mess);
			}
			message.clear();
		};

		submit.setOnAction(sendMsg);
		message.setOnAction(sendMsg);
	
		//Defining TEXTAREA
		ScrollPane board = new ScrollPane();
		board.setFitToWidth(true);
		board.setHbarPolicy(ScrollBarPolicy.NEVER);
		board.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		VBox sp = new VBox();
		boardElems = sp.getChildren();
		board.setContent(sp);
		
		this.boardElems.add(new Text("C'est ici que ca se passe!"));
	
		GridPane.setConstraints(board, 0, 0, 2, 1);
		GridPane.setVgrow(board, Priority.ALWAYS);
		grid.getChildren().add(board);


		Scene scene = new Scene(grid, 300, 275);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Pigeon");

		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		System.out.println("Tuage du pigeon");
		pigeon.kill();
		System.out.println("kthxbye");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
