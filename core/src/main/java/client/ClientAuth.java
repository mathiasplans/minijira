package client;

import messages.ProtocolConnection;
import server.ServerMessage;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class ClientAuth{
    Sync sync;
    ProtocolConnection connection;
    Scanner scan;
    private String username;

    public ClientAuth(Sync sync, ProtocolConnection connection, Scanner scan){
        this.sync = sync;
        this.connection = connection;
        this.scan = scan;
    }

    public boolean login_request() throws IOException{
       System.out.println("Login required, please enter your username: ");
       username = scan.nextLine();
        sync.login(username, null);
       return true;
    }

    public boolean password_request(boolean accepted) throws IOException{
        if (accepted){
            System.out.println("User exists, please enter your password: ");
            String password = scan.nextLine();
            sync.login(username, password);
        }else{
            System.out.println("Username does not exist on this server.");
            System.out.println();
            login_request();
        }
        return true;
    }
}
