/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

/**
 *
 * @author night
 */
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class MensajeServerThread extends Thread {

    private MultiServer server = null;
    private Socket socket = null;
    private SocketAddress ID = null;
    private BufferedInputStream bis = null;
    private DataInputStream dis = null;
    private BufferedOutputStream bos = null;
    private DataOutputStream dos = null;

    public MensajeServerThread(MultiServer server, Socket socket) {
        super();
        this.server = server;
        this.socket = socket;
        ID = socket.getRemoteSocketAddress();
    }

    public SocketAddress getID() {
        return ID;
    }

    public void send(String message) {
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            //System.out.println("Client " + socket.getRemoteSocketAddress() + " error sending : " + e.getMessage());
            server.remove(ID);
        }
    }

    @Override
    public void run() {
        try {
            //System.out.println("Client " + socket.getRemoteSocketAddress() + " connected to server...");

            bis = new BufferedInputStream(socket.getInputStream());
            dis = new DataInputStream(bis);
            bos = new BufferedOutputStream(socket.getOutputStream());
            dos = new DataOutputStream(bos);

            while (true) {
                server.handle(ID, dis.readUTF());
            }
        } catch (IOException e) {
            //System.out.println("Client " + socket.getRemoteSocketAddress() + " error reading : " + e.getMessage());
            server.remove(ID);
        }
    }

    public void close() throws IOException {
        //System.out.println("Client " + socket.getRemoteSocketAddress() + " disconnect from server...");
        socket.close();
        dis.close();
        dos.close();
    }
}
