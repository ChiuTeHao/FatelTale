package tcp;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import cdc.*;

public class ConnectionHandler extends Thread {
    private Socket sock;
    private InputStream is;
    private OutputStream os;
    private Integer id;
    public ConnectionHandler(Socket _sock, int _id) {
        sock = _sock;
        id = new Integer(_id);
        try {
            sock.setKeepAlive(true);
        } catch(SocketException e) {
            System.err.println("An error occur while setting keepalive on socket : " + e);
            System.exit(1);
        }
        try {
            is = sock.getInputStream();
            os = sock.getOutputStream();
        } catch(IOException e) {
            System.err.println("An error occur whlie getting IO stream" + e);
            System.exit(1);
        }
    }
    public void run() {
        System.err.println("[" + id + "]" + " Thread starts");
        try {
            os.write("S".getBytes());
        } catch(IOException e) {
            System.err.println("An error occur whlie sending synchronize message" + e);
            System.exit(1);
        }
        for(;;) {
            try {
                // prase the request message
                int code = is.read();
                int key = 0;
                switch(code) {
                case codes.KEYDOWN:
                    key = is.read();
                    CDC.getInstance().keyDown(id, key);
                case codes.KEYRELEASE:
                    key = is.read();
                    CDC.getInstance().keyRelease(id, key);
                case -1:
                    throw new IOException();
                default:
                    System.err.println("Unrecognized code <" + code + "> ignored");
                }
            } catch(IOException e) {
                System.err.println("Connection closed : " + sock);
                try {
                    TCPServer.getServer().removeConnection(sock.getRemoteSocketAddress());
                } catch(NullPointerException ee){};
                break;
            }
        }
        try {
            sock.close();
        } catch(IOException e) {}
    }
}

