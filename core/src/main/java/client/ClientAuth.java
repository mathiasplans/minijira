package client;

import messages.ProtocolConnection;
import server.ServerMessage;

import java.io.IOException;
import java.util.Scanner;

public class ClientAuth{
    private Sync sync;
    private Scanner scan;
    private String username;

    public ClientAuth(Sync sync, Scanner scan){
        this.sync = sync;
        this.scan = scan;
    }

    public ClientAuth(){
    }

    public boolean loginRequest(String username){
       //System.out.println("Login required, please enter your username: ");
       //username = scan.nextLine();
        this.username = username;
        try{
            sync.login(username, null);
        }catch(IOException e){
            System.out.println("Login failed: " + e.getMessage());
        }
        return true;
    }

    public boolean passwordRequest(boolean accepted) {
        if (accepted){
            System.out.println("User exists, please enter your password: ");
            //String password = scan.nextLine();
            //sync.login(username, password);
        }else{
            System.out.println("Username does not exist on this server.");
            System.out.println();
        }
        return true;
    }

    public void setSync(Sync sync){
        this.sync = sync;
    }

    public void setScan(Scanner scan){
        this.scan = scan;
    }
}
