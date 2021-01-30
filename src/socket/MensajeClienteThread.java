/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author night
 */
public class MensajeClienteThread extends Thread{
    private Socket socket = null;
    private MultiCliente client = null;
    private DataInputStream dis = null;
    
    public MensajeClienteThread(MultiCliente client, Socket socket) {
        this.client = client;
        this.socket = socket;
        open();
        start();
    }

    public void open() {
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error getting input stream : " + e.getMessage());
            client.stop();
        }
    }

    public void close() {
        try {
            dis.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                client.handleMessage(dis.readUTF());
            }
        } catch (IOException e) {
            client.stop();
        }
    }
    
}
