package com.example.iclab03.server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class ChatServer extends Application {

    public static Label logs = new Label("[Chatting Server Logs]");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Thread startServer = new Thread(new StartServer());
        startServer.start();

        ScrollPane layout = new ScrollPane();
        layout.setPrefSize(400, 600);
        layout.setContent(logs);

        primaryStage.setTitle("Chatting server");
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();
    }
}