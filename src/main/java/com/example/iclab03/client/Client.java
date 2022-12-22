package com.example.iclab03.client;

import java.io.*;
import java.net.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Client extends Application {

    public static Label logs = new Label("[Chatting Room Begin]");
    private TextField enterName = new TextField();
    public static TextField enterMessage = new TextField();
    private Scene scene1, scene2;
    private Button submitName = new Button("Join Chatroom");

    public static String name = "Default name";

    private Button uploadFileBtn = new Button("upload file"), sendBtn = new Button("send");

    private final File[] sendFiles = new File[1];

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane root1 = new GridPane();
        root1.setPrefSize(400, 200);
        root1.setPadding(new Insets(0, 20, 20, 20));
        root1.setVgap(15);
        root1.setHgap(5);
        root1.setAlignment(Pos.CENTER);
        root1.add(new Label("Enter your name here: "), 0, 0);
        root1.add(enterName, 0, 1);
        root1.add(submitName, 1, 1);

        scene1 = new Scene(root1);
        primaryStage.setScene(scene1);
        primaryStage.setTitle("Chatting Room");
        primaryStage.show();

        ConnectServer connectServer = new ConnectServer();

        submitName.setOnAction(e -> {
            name = enterName.getText();
            Thread connectServerThread = new Thread(connectServer);
            connectServerThread.start();

            ScrollPane layout = new ScrollPane();
            layout.setPrefSize(400, 600);
            layout.setContent(logs);

            BorderPane root2 = new BorderPane();
            root2.setPrefSize(350, 400);
            root2.setCenter(layout);
            HBox hBox = new HBox();
            hBox.setSpacing(4);
            hBox.setPadding(new Insets(4, 0, 4, 0));
            hBox.setAlignment(Pos.CENTER);
            enterMessage.setPrefWidth(220);
            hBox.getChildren().addAll(enterMessage, uploadFileBtn, sendBtn);
            root2.setBottom(hBox);

            scene2 = new Scene(root2);
            primaryStage.setScene(scene2);
        });

        Client.enterMessage.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                DataOutputStream out = connectServer.getDataOutputStream();
                String msg = Client.enterMessage.getText();
                try {
                    out.writeUTF(msg);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                enterMessage.setText("");
            }
        });

        sendBtn.setOnAction(event -> {
            DataOutputStream out = connectServer.getDataOutputStream();
            String msg = Client.enterMessage.getText();
            try {
                out.writeUTF(msg);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            enterMessage.setText("");

            if (sendFiles.length > 0 && sendFiles[0] != null) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(sendFiles[0].getAbsolutePath());

                    String fileName = sendFiles[0].getName();
                    byte[] fileNameBytes = fileName.getBytes();

                    byte[] fileContentBytes = new byte[(int) sendFiles[0].length()];
                    fileInputStream.read(fileContentBytes);

                    out.writeInt(fileNameBytes.length);
                    out.write(fileNameBytes);

                    out.writeInt(fileContentBytes.length);
                    out.write(fileContentBytes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        uploadFileBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose a file to send");

            if (fileChooser.showOpenDialog(null) != null) {
                sendFiles[0] = fileChooser.showOpenDialog(null);
                System.out.println(sendFiles[0].getName());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class ConnectServer implements Runnable {
    Socket socket = null;
    DataInputStream in = null;
    DataOutputStream out = null;

    public DataOutputStream getDataOutputStream() {
        return out;
    }

    @Override
    public void run() {

        try {
            socket = new Socket("localhost", 1234);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(Client.name);
            System.out.println(Client.name + " : successfully joined the chat room; ");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                String str = in.readUTF();
                Platform.runLater(() -> {
                    Client.logs.setText(Client.logs.getText() + "\n" + str);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}