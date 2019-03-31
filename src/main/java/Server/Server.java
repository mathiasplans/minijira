package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import messages.*;

public class Server {

    public static void main(String[] args) throws Exception {
        try (ServerSocket ss = new ServerSocket(1337)) {
            System.out.println("Server established");
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
                            System.out.println("Client connected, waiting for data");

                            // Message object
                            ServerMessage handler = new ServerMessage();
                            Message messenger = new Message(out, in, handler);

                            String data = messenger.readMessage();
                            System.out.println("Data received: " + data);

//                            switch (messenger.getMessagType()){
//                                case FILE:
//                                    System.out.println("Handled file request");
//                                    break;
//                                case ECHO:
//                                    System.out.println("Echoed the message");
//                                    break;
//                            }

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
