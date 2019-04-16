package client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Scanner;

import common.TaskContainer;
import common.UserContainer;
import messages.*;

public class Client {
    public void clientDaemon() throws IOException {
        try (Socket socket = new Socket("localhost", 1337);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // NOTE: only client side operations are wotking atm. Interaction with server is meaningless
            System.out.println("Connected to localhost:1337");

            // Message object
            ClientMessage handler = new ClientMessage();
            ProtocolConnection messenger = new ProtocolConnection(null, out, in, handler);

            TaskContainer tasks = new TaskContainer(Path.of("src", "test", "resources", "src/test/resources/tasks"));
            UserContainer users = new UserContainer();
            Commands commands = new Commands(tasks, users);

            Scanner scin = new Scanner(System.in);
            String lastCommand;

            while (true){
                if(scin.hasNextLine()){
                    commands.handle(scin.nextLine());
                }
            }
        }
    }
}
