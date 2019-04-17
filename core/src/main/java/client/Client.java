package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Scanner;

import common.Boards;
import common.TaskContainer;
import common.UserContainer;
import messages.*;

/**
 * Class for client part of the minijira
 */
public class Client {
    private final InetAddress address;
    private final int port;

    public Client(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void mainLoop(Socket socket, DataInputStream in, DataOutputStream out) throws IOException {
        // Report success
        if(address == null)
            System.out.println("Connected to localhost:" + port);
        else
            System.out.println("Connected to " + address.getHostAddress() + ":" + port);

        /*
         * UserContainer and TaskContainer. This is the data the server serves.
         * Both containers hold users and tasks respectively.
         */
        UserContainer users = new UserContainer(Path.of("core", "src", "main", "java", "client", "users"));
        TaskContainer tasks = new TaskContainer(Path.of("core", "src", "main", "java", "client", "tasks"));
        Boards boards = new Boards(tasks, Path.of("core", "src", "main", "java", "client", "boards"));


        // Message object
        /*
         * ServerMessage object. Implementation of handling of incoming messages from a client
         */
        ClientMessage handler = new ClientMessage(tasks, users, boards);

        /*
         * ProtocolConnection object. Handles the messages.
         * Server has to call readMessage when message is available
         */
        ProtocolConnection messenger = new ProtocolConnection(null, out, in, handler);

        // Command handler object
        Commands commands = new Commands(tasks, users);

        // Command line input
        Scanner scin = new Scanner(System.in);

        // Main loop
        while (commands.isRunning()) {
            // Handle user input
            if(scin.hasNextLine()){
                commands.handle(scin.nextLine());
            }

            // Read server responses
            if(in.available() != 0){
                // Read a message
                MessageType type = messenger.readMessage();
            }
        }

        // Save data
        users.saveUsers();
        tasks.saveTasks();
        boards.saveBoards();
    }

    public void run() throws IOException {
        if(address == null) {
            try (Socket socket = new Socket("localhost", port);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                // Main loop of the client
                mainLoop(socket, in, out);
            }
        }else{
            try (Socket socket = new Socket(address, port);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                // Main loop of the client
                mainLoop(socket, in, out);
            }
        }
    }
}
