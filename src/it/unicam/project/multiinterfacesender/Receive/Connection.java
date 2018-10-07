package it.unicam.project.multiinterfacesender.Receive;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class Connection implements Runnable {
    private final int DISCONNECTED = 1;
    private final int BEGIN_CONNECT = 3;
    private final int CONNECTED = 4;

    private final String statusMessages[] = {"SRV:\tError! Could not connect!", "SRV:\tDisconnected", "SRV:\tDisconnecting...", "SRV:\tConnecting...", "SRV:\tConnected"};

    private int connectionStatus = BEGIN_CONNECT;

    private Socket socket;
    private TCP_IO IO_Sock = null;

    long connID;
    String dtoken="";
    String dtoken_controparte="";
    String nome_file;
    Connection_Manager connection_manager;

    Connection(Socket clientSocket,Connection_Manager _connection_manager) {
        this.socket = clientSocket;
        this.connection_manager = _connection_manager;
        connID = new Date().getTime();
    }

    private boolean alive = true;

    @Override
    public void run() {
        while (alive) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException ignored) {
            }

            switch (connectionStatus) {
                case BEGIN_CONNECT:
                    onStatus_BEGIN_CONNECT();
                    break;
                case CONNECTED:
                    onStatus_CONNECTED();
                    break;
                case DISCONNECTED:
                    onStatus_DISCONNECTED();
                    break;
                default:
                    break;
            }
        }
    }

    private void onStatus_BEGIN_CONNECT() {
        IO_Sock = new TCP_IO(socket);
        new Thread(IO_Sock).start();
        changeStatus(CONNECTED, true);
        appendLog("(" + connID + ") " + statusMessages[connectionStatus], Color.orange);
    }

    private void onStatus_CONNECTED() {
        try {
            byte[] s;
            System.out.println("CONNECTED");
            if (IO_Sock.in == null) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ignored) {
                }
                return;
            }
            else
            {
                Message o =(Message) IO_Sock.in.readUnshared();
               if(o.dtoken!=null )
               {
                   System.out.println("dtoken="+o.dtoken);
                   dtoken = o.dtoken;
                    if(this.connection_manager.getControparteConnectionByDToken(o.dtoken)!=null)
                    {
                        dtoken_controparte = this.connection_manager.getControparteConnectionByDToken(o.dtoken).dtoken;
                        System.out.println("controparte="+dtoken_controparte);
                    }
               }
               else
               {

                   appendLog("WAN message received:\n\t redirecting [" + o.fileChunk.length + "] bytes TO controparte:"+dtoken_controparte, Color.cyan);
                   Main.sendMessageToClients(o, dtoken_controparte);
                   IO_Sock.out.reset();
               }

            }
        } catch (Exception e) {
            cleanUp();
            changeStatus(DISCONNECTED, false);
        }
    }

    private void onStatus_DISCONNECTED() {
        if (IO_Sock != null) {
            IO_Sock = null;
        }
        alive = false;
        Main.removeClient(this);
    }

    private static int statusPre = 0;

    private void changeStatus(int newConnectStatus, boolean noError) {
        int NULL = 0;
        if (newConnectStatus != NULL)
            connectionStatus = newConnectStatus;
        String statusString;
        if (noError)
            statusString = statusMessages[connectionStatus];
        else
            statusString = statusMessages[NULL];
        int statusPrePre = statusPre;
        statusPre = connectionStatus;
        if (statusPrePre != connectionStatus)
            appendLog("(" + connID + ") " + statusString, Color.orange);
    }

    private void cleanUp() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            socket = null;
        }
        if (IO_Sock.in != null) {
            try {
                IO_Sock.in.close();
            } catch (IOException ignored) {
            }
            IO_Sock.in = null;
        }
        if (IO_Sock.out != null) {
            try {
                IO_Sock.out.close();
            } catch (IOException ignored) {
            }
            IO_Sock.out = null;
        }
    }

    private void appendLog(String msg, Color c) {
        Main.AppendLog(msg, c);
    }

    void SendMesage(Message d) {
        try {
            IO_Sock.out.writeUnshared(d);
        } catch (IOException e) {
            appendLog(e.getMessage(), Color.red);
        }
        try {
            IO_Sock.out.flush();
        } catch (IOException e) {
            appendLog(e.getMessage(), Color.red);
        }
        try {
            IO_Sock.out.reset();
        } catch (IOException e) {
            appendLog(e.getMessage(), Color.red);
        }
    }

    void disconnect() {
        cleanUp();
        changeStatus(DISCONNECTED, true);
    }
}
