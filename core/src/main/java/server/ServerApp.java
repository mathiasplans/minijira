package server;

public class ServerApp {
    public static void main(String[] args) {
        // Create a server bound to localhost:1337
        Server server = new Server(null, 1337);

        // Main thread
        Thread serverThread = new Thread(server);

        // Activate the thread
        serverThread.start();
    }
}
