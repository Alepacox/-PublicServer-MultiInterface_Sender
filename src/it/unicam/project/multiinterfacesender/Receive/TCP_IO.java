package it.unicam.project.multiinterfacesender.Receive;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCP_IO implements Runnable {
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    private Socket socket;

    boolean ready = false;

    TCP_IO(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (out == null || in == null) {
            if (out == null) {
                try {
                    out = new ObjectOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in == null) {
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ready = true;
    }
}