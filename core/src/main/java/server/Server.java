package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

import common.Boards;
import common.TaskContainer;
import common.UserContainer;
import messages.*;

public class Server implements Runnable {
    private final InetAddress address;
    private final int port;

    /**
     * Main constructor. Initializes the address and port fields
     * @param address
     * @param port
     */
    public Server(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Main loop of the server
     * @param ss ServerSocket object
     * @throws IOException If construction of UserContainer and TaskContainer fails
     */
    private void mainLoop(ServerSocket ss) throws IOException {
        // Do server stuff
        while(true) {
            if(address == null)
                System.out.println("Now listening to localhost:" + port);
            else
                System.out.println("Now listening to " + address.getHostAddress() + ":" + port);

            /*
             * UserContainer and TaskContainer. This is the data the server serves.
             * Both containers hold users and tasks respectively.
             */
            UserContainer users = new UserContainer(Path.of("core", "src", "main", "java", "server", "users"));
            TaskContainer tasks = new TaskContainer(Path.of("core", "src", "main", "java", "server", "tasks"));
            Boards boards = new Boards(tasks, Path.of("core", "src", "main", "java", "server", "boards"));

            // Accept a connection from a client
            try {
                Socket socket = ss.accept();

                // Report success
                System.out.println("Connection established, trying to create thread");

                // Create a thread for the client
                Thread thread = new Thread(() -> {
                    // IO objects
                    try(
                            socket;
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            DataInputStream in = new DataInputStream(socket.getInputStream())
                    ){
                        // Report success
                        System.out.println("client connected, waiting for data");

                        // Message object
                        /*
                         * ServerMessage object. Implementation of handling of incoming messages from a client
                         */
                        ServerMessage handler = new ServerMessage(tasks, users, boards);

                        /*
                         * ProtocolConnection object. Handles the messages.
                         * Server has to call readMessage when message is available
                         */
                        ProtocolConnection messenger = new ProtocolConnection(null, out, in, handler);

                        // Set the messenger as handler connection, enables handler to send response messages
                        handler.setConnection(messenger);

                        /* Body of the thread */

                        while (true){
                            if(in.available() != 0){
                                // Read a message
                                MessageType type = messenger.readMessage();
                            }
                        }

                        /* End of body */

                    } catch (IOException e){
                        System.out.println("Thread failed: " + Thread.currentThread().getId());
                        throw new RuntimeException(e);
                    }
                });

                // Start the thread
                thread.start();

            }catch (IOException e){
                throw new RuntimeException("Socket accept failed", e);
            }
        }
    }

    @Override
    public void run() {
        if(address == null) {
            // Establish a server connection
            try (ServerSocket ss = new ServerSocket(port)) {
                System.out.println("Server established on localhost:" + port);

                mainLoop(ss);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            try (ServerSocket ss = new ServerSocket(port, 0, address)) {
                System.out.println("Server established on " + address.getHostAddress() + ":" + port);

                mainLoop(ss);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
