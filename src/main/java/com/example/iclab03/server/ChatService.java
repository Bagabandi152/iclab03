package com.example.iclab03.server;

import java.io.DataInputStream;
import java.net.Socket;

public class ChatService implements Runnable {

    Socket socket;
    DataInputStream in;
    String name;
    User user;

    public ChatService(User user, Socket socket) throws Exception {
        this.user = user;
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());

        this.name = in.readUTF();
        user.addClient(name, socket);
    }

    public void run() {
        try {
            while (true) {
                String msg = in.readUTF();
                user.sendMsg(msg, name);
            }
        } catch (Exception e) {
            user.removeClient(this.name);
        }
    }
}