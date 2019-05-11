package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import common.Boards;
import common.TaskContainer;
import common.UserContainer;
import messages.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    private void mainLoop(Socket socket, DataInputStream in, DataOutputStream out) throws IOException {
        // Report success
        if(address == null)
            System.out.println("Connected to localhost:" + port);
        else
            System.out.println("Connected to " + address.getHostAddress() + ":" + port);

        // Paths to container saves
        Path userPath = Path.of("data", "client", "users");
        Path taskPath = Path.of("data", "client", "tasks");
        Path boardPath = Path.of("data", "client", "boards");

        /*
         * UserContainer and TaskContainer. This is the data the server serves.
         * Both containers hold users and tasks respectively.
         */
        UserContainer users = new UserContainer(userPath);
        TaskContainer tasks = new TaskContainer(taskPath);
        Boards boards = new Boards(tasks, boardPath);

        ClientAuth authentification = new ClientAuth(); //Empty constructor, fields added later: avoids dependency conflicts

        // Message object
        /*
         * ServerMessage object. Implementation of handling of incoming messages from a client
         */
        ClientMessage handler = new ClientMessage(tasks, users, boards, authentification);

        /*
         * ProtocolConnection object. Handles the messages.
         * Server has to convert readMessage when message is available
         */
        ProtocolConnection messenger = new ProtocolConnection(null, out, in, handler);

        // Synchronisation object
        Sync sync = new Sync(messenger, in);

        // Command handler object
        Commands commands = new Commands(tasks, users, boards, messenger, sync, authentification);

        // Command line input
        Scanner scin = new Scanner(System.in);

        // Initialise authentification
        authentification.setSync(sync);
        authentification.setScan(scin);

        //authentification.loginRequest();

        // Quick intro
        System.out.println("Minijira 1.0\nUse 'man' for manual");

        // Main loop
        while (commands.isRunning()) {
            // Handle user input
            if(scin.hasNextLine()){
                commands.handle(scin);
            }
        }

        // Save data
//        users.saveUsers();
//        tasks.saveTasks();
//        boards.saveBoards();
    }

    /**
     * Method for producing a Socket object. If address is not given, bind it
     * to localhost
     * @return
     * @throws IOException
     */
    @NotNull
    @Contract(" -> new")
    private Socket getSocket() throws IOException {
        if(address == null)
            return new Socket("localhost", port);
        else
            return new Socket(address, port);
    }

    public void run() throws IOException {
        try(Socket socket = getSocket();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())){

            mainLoop(socket, in, out);
        }
    }
}
