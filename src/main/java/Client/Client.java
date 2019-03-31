package Client;

import java.io.*;
import java.net.Socket;

import messages.*;

public class Client {
    public static void main(String[] args) throws Exception {
        String function = args[0], line_out = args[1], line_in;

        try {
            line_in = args[2];
        }catch (Exception e){
            line_in = "";
        }

        try (Socket socket = new Socket("localhost", 1337);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            System.out.println("Connected to localhost:1337");

            // Message object
            ClientMessage handler = new ClientMessage();
            Message messenger = new Message(out, in, handler);
        }
    }
}
