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

                int fileNameLength = in.readInt();
                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    in.readFully(fileNameBytes, 0, fileNameLength);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = in.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        in.readFully(fileContentBytes, 0, fileContentLength);
                        user.sendFile(fileName, name);
                    }
                }
            }
        } catch (Exception e) {
            user.removeClient(this.name);
        }
    }
}