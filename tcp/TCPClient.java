package tcp;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.lang.Thread;
import java.util.concurrent.CyclicBarrier;
import dom.DOM;
import sdm.SDM;
import java.nio.ByteBuffer;
import ui.UI;

import logger.Logger;

public class TCPClient extends Thread{
    private InputStream is = null;
    private OutputStream os = null;
    private Socket sock = null;
    private Integer clientid = null;
    private int player_num = 0;
    private CyclicBarrier ready_barrier = new CyclicBarrier(2);
    private static TCPClient client = null;
    public static final int DEFAULT_PORT = 8888;
    private TCPClient() {}
    public Integer getClientNo() { return clientid; }
    public static TCPClient getClient() {
        if(client == null)
            client = new TCPClient();
        return client;
    }
    public boolean connectServer(String addr) {
        InetAddress srv = null;
        try {
            srv = InetAddress.getByName(addr);
        } catch (UnknownHostException e) {
            Logger.log("Cannot connect to server");
            return false;
        }
        return connectServer(srv);
    }
    public boolean connectServer(InetAddress srv) {
        if(client == null)
            client = new TCPClient();
        try {
            sock = new Socket(srv, DEFAULT_PORT);
            sock.setKeepAlive(true);
        } catch(IllegalArgumentException e) {
            Logger.log("Invalid server address");
            return false;
        } catch(IOException e) {
            Logger.log("Connection failed.");
            return false;
        }
        try {
            is = sock.getInputStream();
            os = sock.getOutputStream();
        } catch(IOException e) {
            Logger.log("An error occur while getting IO stream : " + e);
            return false;
        }

        Logger.log("Connected to server : " + sock);
        Logger.log("Waiting for other clients...");

        try {
            int syn = is.read();
            if(syn != codes.SYN) {
                Logger.log("Receive an invalid synchronize message : <" + syn + "> from" + sock);
                return false;
            }
        } catch(IOException e) {
            Logger.log("An error occure while reading from socket : " + sock);
            return false;
        }
        try {
            clientid = is.read();
        } catch(IOException e) {
            Logger.log("An error occure while reading from socket : " + sock);
            return false;
        }
        Logger.log("Delivered client ID " + clientid);
        try {
            player_num = is.read();
        } catch(IOException e) {
            Logger.log("An error occure while reading from socket : " + sock);
            return false;
        }
        for(int i = 0; i < player_num; ++i) {
            DOM.getInstance().addPlayer(i);
            DOM.getInstance().addPlayerInfo(i);
        }
        DOM.getInstance().setClientno(clientid);
        try {
            ready_barrier.await();
        } catch(Exception e) {
            Logger.log("Ready_barrier await failed : " + e);
            return false;
        }
        Logger.log("All clients connected.");
        return true;
    }
    public void keyDown(int code) {
        try {
            if(os == null) return;
            os.write(codes.KEYDOWN);
            os.write(code);
        } catch(IOException e) {
            Logger.log("An error occur while sending to server : " + e);
        }
    }
    public void keyRelease(int code) {
        try {
            if(os == null) return;
            os.write(codes.KEYRELEASE);
            os.write(code);
        } catch(IOException e) {
            Logger.log("An error occur while sending to server : " + e);
        }
    }
    public void waitForReady() {
        try {
            ready_barrier.await();
        } catch(Exception e) {
        }
    }
    public void run() {
        for(;;) {
            try {
                int code = is.read();
                int objid = 0, type = 0;;
                byte[] bytes = new byte[4];
                switch(code) {
                case codes.CREATEOBJ:
                    is.read(bytes, 0, 4);
                    objid = ByteBuffer.wrap(bytes).getInt();
                    type = is.read();
                    switch(type) {
                        case codes.PLAYER:
                            DOM.getInstance().addPlayer(objid);
                            break;
                        case codes.PROJECTOR:
                            Logger.log("Create projector " + objid);
                            DOM.getInstance().addProjector(objid);
                            break;
                        case codes.MONSTER:
                            DOM.getInstance().addMonster(objid);
                            break;
                        case codes.ITEM:
                            DOM.getInstance().addItem(objid);
                            break;
                    }
                    break;
                case codes.REMOVEOBJ:
                    is.read(bytes, 0, 4);
                    objid = ByteBuffer.wrap(bytes).getInt();
                    type = is.read();
                    switch(type) {
                        case codes.PLAYER:
                            DOM.getInstance().removePlayer(objid);
                            break;
                        case codes.PROJECTOR:
                            Logger.log("Remove projector " + objid);
                            DOM.getInstance().removeProjector(objid);
                            break;
                        case codes.MONSTER:
                            DOM.getInstance().removeMonster(objid);
                            break;
                        case codes.ITEM:
                            DOM.getInstance().removeItem(objid);
                            break;
                    }
                    break;
                case codes.READMAP:
                    int len = is.read();
                    byte[] buf = new byte[len];
                    is.read(buf, 0, len);
                    SDM.getInstance().readMap((new String(buf)));
                    break;
                case codes.END:
                    UI.getInstance().endGameScreen();
                    break;
                case codes.SETDEAD:
                    DOM.getInstance().setPlayerHealth(0);
                    break;
                case -1:
                    throw new IOException();
                default:
                    Logger.log("Unrecognized code <" + code + "> ignored");
                }
            } catch(IOException e) {
                Logger.log("Connection closed : " + sock);
                break;
            }
        }
        try {
            sock.close();
        } catch(IOException e) {}
    }
}

