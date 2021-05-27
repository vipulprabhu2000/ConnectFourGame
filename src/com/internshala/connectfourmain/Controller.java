package com.internshala.connectfourmain;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sun.security.mscapi.CPublicKey;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// the main purpose of clickable rectangle i.e hover over rect is to get the disc inserted
public class Controller implements Initializable {//1 declaring all the constants which are going to be same throughtout the game
	private static final int Col = 7;
	private static final int Row = 6;
	private static final int diameter = 80;
	private static final String disc1Col = "#24303E";
	private static final String disc2Col = "#4CAA88";

	private static final String player1name = "Player One";
	private static final String player2name = "Player Two";
	private boolean turnplayerone = true; // always player one plays first
	private Disc[][] inserteddiscarray = new Disc[Row][Col];// for structural changes we are looking at the rectangle with holes as a collection of objects in the two D array

	@FXML
	public Label PlayerNameone;
	@FXML
	public Label PlayerNametwo;

	@FXML
	public GridPane rootgridpane;
	@FXML
	public Label Playerone;
	@FXML
	public Pane inserteddiscpane;
	private boolean isallowed=true;
	public void background() {
		Shape shapewithcircle = structure();
		rootgridpane.add(shapewithcircle, 0, 1);
		// hoverover rectangle to check the user where he is trying to insert the disc
		List<Rectangle> rectangleList = createhoverrect();
		for (Rectangle rectangle : rectangleList) {
			rootgridpane.add(rectangle, 0, 1);

		}


	}

	private Shape structure() {

		Shape shapewithcircle = new Rectangle((Col + 1) * diameter, (Row + 1) * diameter);

		for (int row = 0; row < Row; row++) {
			for (int col = 0; col < Col; col++) {
				Circle circle = new Circle();
				circle.setRadius(diameter / 2);
				circle.setCenterX(diameter / 2);   //this statements creates circles at the same locations so inorder to get it over entire rectangle we use translate statements
				circle.setCenterY(diameter / 2);
				circle.setSmooth(true);
				circle.setTranslateX(col * (diameter + 5) + (diameter / 4));// cut holes to get the circles at incremented locations
				circle.setTranslateY(row * (diameter + 5) + (diameter / 4));

				shapewithcircle = Shape.subtract(shapewithcircle, circle);
			}
		}

		shapewithcircle.setFill(Color.WHITE);
		return shapewithcircle;
	}

	private List<Rectangle> createhoverrect() {// now creating a list to collect objects so to create a list of rectangles
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < Col; col++) {
			Rectangle rectangle = new Rectangle(diameter, (Row + 1) * diameter);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (diameter + 4) + (diameter / 4) + (diameter / 4) - 19);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int colum = col;
			rectangle.setOnMouseClicked(event -> {
				if (isallowed) {
					isallowed=false;  //if double clicked . then only once the player is allowed to play
					insertdisc(new Disc(turnplayerone), colum);
				}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertdisc(Disc disc, int column) {
		int rows = Row - 1;
		while (rows >= 0) {
			if (getdiscIfpresent(rows,column) == null)
				break;     // checking if there a=is space to add the circle at that postion as column is already supplied we are just keeping the col same and chnging the rows to check if the rows is filled for a given column position
			rows--;
		}

		if (rows < 0)
			return;   // do nothing


		inserteddiscarray[rows][column] = disc;// for developer
		inserteddiscpane.getChildren().add(disc);  //for user to see
		disc.setTranslateX(column * (diameter + 4) + (diameter / 4) + (diameter / 4) - 19); //these make the disc appear at the top

		int currentrow = rows;// there was an error at 	if(gameEnded(rows,column))

		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), disc);
		transition.setToY(rows * (diameter + 5) + (diameter / 4));
		transition.setOnFinished(event -> {
			isallowed=true;  //next player is allowed to insert the disc
			if (gameEnded(currentrow, column)) {
				gameover();
				return;

			}
			turnplayerone = !turnplayerone;  //these makes player two to play so colour changes
			Playerone.setText(turnplayerone ? player1name : player2name);
		});
		transition.play();

	}

	private boolean gameEnded(int r, int c) {   //vertical checking for combinations
		List<Point2D> verticalpoints = IntStream.rangeClosed(r - 3, r + 3)  // ex:- range of row values=0,1,2,3,4,5
				.mapToObj(r1 -> new Point2D(r1, c)) //0,3  1,3  2,3  3,3  4,3  5,3->points2d
				.collect(Collectors.toList());
		List<Point2D> horizontalpoints = IntStream.rangeClosed(c - 3, c + 3)
				.mapToObj(c1 -> new Point2D(r, c1))
				.collect(Collectors.toList());

		Point2D startpoint1= new Point2D(r-3,c+3);
		List<Point2D> diagonal1points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startpoint1.add(i, -i))
				.collect(Collectors.toList());

		Point2D startpoint2= new Point2D(r-3,c-3);
		List<Point2D> diagonal2points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startpoint2.add(i, i))
				.collect(Collectors.toList());

		///to check out possible combinations
		boolean isEnded = checkCombinations(verticalpoints)|| checkCombinations(horizontalpoints)
				||checkCombinations(diagonal1points)||checkCombinations(diagonal2points);
		return isEnded;

	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;// so 4 circle makes a combinations for this we creating these we are creating these var

		for (Point2D point : points) {

			int rowindexforarray = (int) point.getX();
			int colindexforarray = (int) point.getY();
			Disc disc = getdiscIfpresent(rowindexforarray,colindexforarray );
			if (disc != null && disc.turnplayermove== turnplayerone) {
				chain++;
				if (chain ==4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}
		return false;

	}
	private  Disc getdiscIfpresent(int row1,int column1){
		// to prevent arrayindexoutofboundexception
		if(row1>=Row || row1<0 || column1>= Col ||column1<0)
			return null;

		return inserteddiscarray[row1][column1];


	}
	private void gameover() {
		String winner= turnplayerone? player1name:player2name;
		System.out.println("winner is :"+ winner);
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The Winner is "+winner);
		alert.setContentText("Want to play again");

		ButtonType yesbtn=new ButtonType("yes");
		ButtonType nobtn=new ButtonType("No,Exit");

		alert.getButtonTypes().setAll(yesbtn,nobtn);
		Platform.runLater(()->{
			Optional<ButtonType> buttonclicked = alert.showAndWait();
			if( buttonclicked.isPresent() && buttonclicked.get()==yesbtn){
				//reset game
				resetGame();
			}else
			{  Platform.exit();// this exits the app
				System.exit(0);

			}

		});

	}

	public void resetGame() {
		inserteddiscpane.getChildren().clear();
		for (int row=0;row< inserteddiscarray.length;row++)
		{
			for (int col = 0; col < inserteddiscarray[row].length; col++) {//structurally ,//make all elemnts inserted in array null
				inserteddiscarray[row][col]=null;

			}
		}
		turnplayerone=true;//let player start the game
		Playerone.setText(player1name);
		createhoverrect();//prepares new playground .
	}

	//these class helps to differentiate between two discs
	private static class Disc extends Circle {

		private final boolean turnplayermove;

		public Disc(boolean turnplayermove) {
			this.turnplayermove = turnplayermove;
			setRadius(diameter / 2);
			setFill(turnplayermove ? Color.valueOf(disc1Col) : Color.valueOf(disc2Col));
			setCenterX(diameter / 2);
			setCenterY(diameter / 2);

		}


	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}