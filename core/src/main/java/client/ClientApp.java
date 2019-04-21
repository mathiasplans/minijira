package client;

import java.io.IOException;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        // Create a client object bound to localhost:1337
        Client client = new Client(null, 1337);

        // Do the client stuff
        client.run();
    }
}
