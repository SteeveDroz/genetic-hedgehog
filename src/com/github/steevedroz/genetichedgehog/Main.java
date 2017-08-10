package com.github.steevedroz.genetichedgehog;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
	try {
	    BorderPane root = new BorderPane();
	    Scene scene = new Scene(root, 800, 800);
	    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    root.setCenter(new GeneticHedgehog());
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Genetic Hedgehog v0.1");
	    primaryStage.show();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	launch(args);
    }
}
