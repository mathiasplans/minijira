import client.Client;

import java.io.IOException;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        Client client = new Client();

        // Guide
        System.out.println("Praegu saab klienis ainult lokaalsete taskidega interakteeruda. Proovida commande:\n" +
                "task create <nimi>\n" +
                "task info <id> <-- siinpuhul 0, kuna id kasvab igal loomisel\n" +
                "task complete <id>\n" +
                "save <-- salvestab taskid kausta");

        client.clientDaemon();
    }
}
