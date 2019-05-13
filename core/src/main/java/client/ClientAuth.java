package client;

import messages.ProtocolConnection;
import server.ServerMessage;

import java.io.IOException;
import java.util.Scanner;

public class ClientAuth{
    private Sync sync;
    private String username;
    private String password;
    private int state; /** state: 0 = idle, 1 = logging in, 2 = registering, 3 = logged in **/

    public ClientAuth(Sync sync){
        this.sync = sync;
        this.state = 0;
    }

    public ClientAuth(){
    }

    /**
     * Checks if user exists on the server
     * [username, null]
    **/
    public void userExistsRequest(String username){
        this.username = username;
        try{
            state = 1;
            sync.login(username, null);
        }catch(IOException e){
            System.out.println("Request failed: " + e.getMessage());
        }
    }

    public void doesNotExist(){
        System.out.println("User \"" + username + "\" does not exist on this server");
        state = 0;
        this.username = null;
        this.password = null;
    }

    /**
     * Attempts to log in as user previuosly sent via userExistsRequest
     * [null, password]
     **/
    public void loginRequest(String username, String password){
        this.password = password;
        if(this.username != null && this.username.equals(username)){
            try{
                sync.login(null, password);
              }catch(IOException e){
                 System.out.println("Login failed: " + e.getMessage());
             }
        }else{
            userExistsRequest(username);
        }
    }

    public void loginRequest(){
        if(state == 1){
            try{
                sync.login(null, password);
            }catch(IOException e){
                System.out.println("Login failed: " + e.getMessage());
                e.printStackTrace();
            }
        }else{
            System.out.println("Unexpected reply from server");
        }
    }

    public void loginConfirmed(){
        System.out.println("Successfully logged in. Welcome, " + username + "!");
        state = 3;
        this.password = null;
    }

    public void wrongPassword(){
        System.out.println("Wrong password for user " + username + "; try logging in again");
        state = 0;
        this.username = null;
        this.password = null;
    }

    /**
     * Initiates registration procedure
     * [username, password]
     **/
    public void registerRequest(String username, String password){
        this.username = username;
        this.password = password;
        try{
            sync.login(username, password);
            state = 2;
        }catch(IOException e){
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    public void registrationSuccessful(){
        System.out.println("You have been registered successfully. Welcome, " + username + "!");
        state = 3;
        this.password = null;
    }

    public void alreadyExists(){
        System.out.println("Cannot register, user named \"" + username + "\" already exists.");
        state = 0;
        this.username = null;
        this.password = null;
    }

    /**
     * Cancels login/registration request (erases stored username value, ready for next attempt; also logout)
     * [null, null]
     **/
    public void logoutRequest(){
        try{
            sync.login(null, null);
        }catch(IOException e){
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    public void loginCancelled(){
        System.out.println("Login procedure cancelled");
        state = 0;
        this.username = null;
        this.password = null;
    }

    public void loggedOut(){
        System.out.println("User " + username + " successfully logged out!");
        state = 0;
        this.username = null;
        this.password = null;
    }

    public int getState(){
        return state;
    }

    public void setSync(Sync sync){
        this.sync = sync;
    }
}
