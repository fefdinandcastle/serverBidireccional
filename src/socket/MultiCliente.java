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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements java socket client
 * @author pankaj
 *
 */
public class MultiCliente implements Runnable {
    private String serverName = "localhost";
    private int serverPort = 9876;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private MensajeClienteThread client = null;
    private int n = 1;
    int recvNumber = 0;
    boolean status = false;
    int sendCount = 0;

    public MultiCliente() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort() + "...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            dis = new DataInputStream(System.in);
            dos = new DataOutputStream(socket.getOutputStream());
            client = new MensajeClienteThread(this, socket);
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                if(sendCount<5){
                    if(status == false){
                        dos.writeUTF("first");
                        dos.flush();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MultiCliente.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else{
                        recvNumber++;
                        if(sendCount == 5){
                            System.out.print("\nSaliendo...");
                            String s = "exit";
                            dos.writeUTF(s);
                            dos.flush();                   
                        }else{
                            System.out.print("\nEnviando mensaje a servidor "+recvNumber+"\n");
                            String s = Integer.toString(recvNumber);
                            dos.writeUTF(s);
                            dos.flush();
                            sendCount++;

                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MultiCliente.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        
                    }
                    
                }else{
                    System.out.print("\nSaliendo...");
                    String s = "exit";
                    dos.writeUTF(s);
                    dos.flush();
                }
                
                
                // Sleep, because this thread must wait ChatClientThread to show the message first
                try {
                    thread.sleep(600);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("Sending error : " + e.getMessage());
                stop();
            }
        }
    }

    public void handleMessage(String message) {
        if (message.equals("exit")) {
            stop();
        } else {
            //System.out.println(message);
            status = true;
            String x = String.valueOf(message.charAt(0));
            if(message.length()>1&&message.charAt(1)!=' '){
                x = ""+message.charAt(0)+message.charAt(1);
            }
            
            //System.out.println(tmp);
            recvNumber = Integer.valueOf(x);
            System.out.print("\nMessage from server : "+message);
        }
    }

    public void stop() {
        try {
            thread = null;
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing : " + e.getMessage());
        }
        client.close();
    }

    public static void main(String args[]) {
        MultiCliente client = new MultiCliente();
    }
}
