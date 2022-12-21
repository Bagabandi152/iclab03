package com.example.iclab03.server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import javafx.application.Platform;

public class User {
    HashMap<String, DataOutputStream> clientMap = new HashMap<>();
    String str;

    public synchronized void addClient(String name, Socket socket) {
        try {
            clientMap.put(name, new DataOutputStream(socket.getOutputStream()));
            sendMsg(name + " joined.", "Server");
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public synchronized void removeClient(String name) {
        try {
            clientMap.remove(name);
            sendMsg(name + " exit.", "Server");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public synchronized void sendMsg(String msg, String name) throws Exception {
        str = name + " : " + msg;
        Platform.runLater(() -> {
            ChatServer.logs.setText(ChatServer.logs.getText() + "\n" + str);
        });

        Iterator iterator = clientMap.keySet().iterator();
        while (iterator.hasNext()) {
            String clientName = (String) iterator.next();
            clientMap.get(clientName).writeUTF(name + " : " + msg);
        }
    }
}

