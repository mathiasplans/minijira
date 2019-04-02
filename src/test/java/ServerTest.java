import server.Server;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        server.serverDaemon();
    }
}
