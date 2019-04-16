package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import messages.*;

public class Server {
    public void serverDaemon() throws IOException{
        try (ServerSocket ss = new ServerSocket(1337)) {
            System.out.println("server established");
            while(true) {
                System.out.println("Now listening to localhost:1337");
                try {
                    Socket socket = ss.accept();
                    System.out.println("Connection established, trying to create thread");
                    Thread thread = new Thread(() -> {
                        // IO objects
                        try(
                                socket;
                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                DataInputStream in = new DataInputStream(socket.getInputStream())
                        ){
                            System.out.println("client connected, waiting for data");

                            // Message object
                            ServerMessage handler = new ServerMessage();
                            ProtocolConnection messenger = new ProtocolConnection(null, out, in, handler);


                        } catch (IOException e){
                            System.out.println("Thread failed");
                            throw new RuntimeException(e);
                        }
                    });

                    thread.start();
                }catch (IOException e){
                    throw new RuntimeException("Socket accept failed", e);
                }
            }
        }
    }
}
