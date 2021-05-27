package com.internshala.connectfourmain;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridpane;
		rootGridpane = loader.load();

		controller = loader.getController();
		controller.background();

		Scene scene = new Scene(rootGridpane);

		MenuBar menubar = createmenu();//3
		menubar.prefWidthProperty().bind(primaryStage.widthProperty());
		Pane menupane = (Pane) rootGridpane.getChildren().get(0);
		menupane.getChildren().add(menubar);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four");
		primaryStage.setResizable(false);
		primaryStage.show();


	}





	private MenuBar createmenu() {
		//1filemenu
		Menu filemenu = new Menu("File");

		MenuItem newitem = new MenuItem("New");
		newitem.setOnAction(event -> controller.resetGame());
		MenuItem resetitem = new MenuItem("Reset");
		resetitem.setOnAction(event ->controller.resetGame());
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exititem = new MenuItem("Exit");
		exititem.setOnAction(event -> exitGame());
		filemenu.getItems().addAll(newitem, resetitem, exititem);
		//2 about
		Menu aboutmenu = new Menu("Help");

		MenuItem aboutitem1 = new MenuItem("About Game");
		aboutitem1.setOnAction(event -> aboutGame());
		MenuItem aboutitem2 = new MenuItem("About Developer");
		aboutitem2.setOnAction(event -> aboutDev());
		aboutmenu.getItems().addAll(aboutitem1, aboutitem2);


		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(filemenu, aboutmenu);

		return menuBar;


	}

	private void aboutDev() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText("Vipul Prabhu Shirodkar");
		alert.setContentText("I am a beginner but soon will become a pro in developing" +
				" some good applications in java. " +
				"Connect 4 is one the applications i developed recently");
		alert.show();
	}

	private void aboutGame() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Message");
		alert.setHeaderText("How to Play?");
		alert.setContentText("Connect Four is a two-player connection game in which " +
				"the players first choose a color and then take turns dropping " +
				"colored discs from the top into a seven-column, six-row vertically suspended grid." +
				" The pieces fall straight down, " +
				"occupying the next available space within the column. " +
				"The objective of the game is to be the first to form a horizontal, vertical, " +
				"or diagonal line of four of one's own discs. Connect Four is a solved game. " +
				"The first player can always win by playing the right moves.");
		alert.show();

	}

	private void exitGame() {
		Platform.exit();// this exits the app
		System.exit(0);//exit the threads as well which are created during the app functioning
	}



	public static void main(String[] args) {
		launch(args);
	}
}