/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 *
 * @author night
 */
public class MultiServer implements Runnable {

    private int port = 9876;
    private ServerSocket serverSocket = null;
    private Thread thread = null;
    private MensajeServerThread clients[] = new MensajeServerThread[50];
    private int clientCount = 0;

    public MultiServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + serverSocket.getLocalPort() + "...");
            System.out.println("Waiting for client...");
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            System.out.println("Can not bind to port : " + e);
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                // wait until client socket connecting, then add new thread
                addThreadClient(serverSocket.accept());
            } catch (IOException e) {
                System.out.println("Server accept error : " + e);
                stop();
            }
        }
    }

    public void stop() {
        if (thread != null) {
            thread = null;
        }
    }

    private int findClient(SocketAddress ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void handle(SocketAddress ID, String input) {
        if (input.equals("exit")) {
            clients[findClient(ID)].send("exit");
            for (int i = 0; i < clientCount; i++) {
                if(clients[i].getID() == ID){
                    int id = i+1;
                    System.out.println("\nEl cliente " + id + " se ha desconectado del servidor");
                    // if this client ID is the sender, just skip it
                    continue;
                }
                //clients[i].send("\n" + ID + " says : " + input);
                
            }
            remove(ID);
        } else {
            
            for (int i = 0; i < clientCount; i++) {
                if(clients[i].getID() == ID){
                    int id = i+1;
                    System.out.println("\nCliente " + id + " dice : " + input);
                    // if this client ID is the sender, just skip it
                    continue;
                }
                //clients[i].send("\n" + ID + " says : " + input);
                
            }
        }
    }

    public synchronized void remove(SocketAddress ID) {
        int index = findClient(ID);
        if (index >= 0) {
            MensajeServerThread threadToTerminate = clients[index];
            //System.out.println("Removing client thread " + ID + " at " + index);
            if (index < clientCount - 1) {
                for (int i = index + 1; i < clientCount; i++) {
                    //clients[i - 1] = clients[i];
                }
            }
            //clientCount--;
            try {
                threadToTerminate.close();
            } catch (IOException e) {
                System.out.println("Error closing thread : " + e.getMessage());
            }
        }
    }

    private void addThreadClient(Socket socket) {
        if (clientCount < clients.length) {
            clients[clientCount] = new MensajeServerThread(this, socket);
            clients[clientCount].start();
            clientCount++;
            System.out.println("\nSe ha conectado el cliente "+clientCount+" al servidor.");
        } else {
            System.out.println("Client refused : maximum " + clients.length + " reached.");
        }
    }

    public static void main(String[] args) {
        MultiServer server = new MultiServer();
    }
}
