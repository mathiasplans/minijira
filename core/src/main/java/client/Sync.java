package client;

import common.Permissions;
import common.Task;
import data.RawLogin;
import data.RawProject;
import data.RawTask;
import data.RawUser;
import messages.MessageType;
import messages.ProtocolConnection;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Set;

public class Sync {
    private final ProtocolConnection connection;
    private final DataInputStream in;

    public Sync(ProtocolConnection connection, DataInputStream in){
        this.connection = connection;
        this.in = in;
    }

    private MessageType waitForResponse() throws IOException {
        return connection.readMessage();
    }

    /**
     * Update task request. Should be responded to with RESPONSE
     * UPDATETASK -> RESPONSE
     * @param task
     * @throws IOException
     */
    public void updateTask(Task task) throws IOException {
        // Send the message
        connection.sendMessage(task.getRawTask(), MessageType.UPDATETASK);

        // Wait for RESPONSE type message
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.RESPONSE){
            // Handle Error
            throw new InvalidResponseException("Expected RESPONSE, received " + responseType.name());
        }
    }

    /**
     * Task creation request. Should be responded to with UPDATETASK (Gets ID from server)
     * CREATETASK -> UPDATETASK
     * @param task
     * @throws IOException
     */
    public void createTask(@NotNull Task task) throws IOException {
        // Send the message
        connection.sendMessage(task.getRawTask(), MessageType.CREATETASK);

        // Wait for UPDATETASK type message
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.UPDATETASK){
            // Handle error
            throw new InvalidResponseException("Expected UPDATETASK, received " + responseType.name());
        }
    }

    /**
     * Task removal request. Should be responded to with RESPONSE
     * REMOVETASK -> RESPONSE
     * @param task
     * @throws IOException
     */
    public void removeTask(Task task) throws IOException {
        // Send the message
        connection.sendMessage(task.getId(), MessageType.REMOVETASK);

        // Wait for RESPONSE type message
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.RESPONSE){
            // Handle error
            throw new InvalidResponseException("Expected RESPONSE, received " + responseType.name());
        }
    }

    /**
     * Gets all the tasks in the board. Should be responded to with SETPROJECT
     * GETPROJECT -> SETPROJECT
     * @param id
     * @throws IOException
     */
    public void getBoardTasks(Long id) throws IOException {
        // Send the message
        connection.sendMessage(id, MessageType.GETPROJECT);

        // Wait for response and handle it
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.SETPROJECT){
            // Handle error
            throw new InvalidResponseException("Expected SETPROJECT, received " + responseType.name());
        }
    }

    /**
     * Gets the tasks of specified boards (given as the argument).
     * @param boards
     * @throws IOException
     */
    public void getTasks(@NotNull Set<Long> boards) throws IOException {
        // For every board, send the query
        // TODO: this is costly, tasks can be in multiple boards
        for(Long boardID: boards){
            getBoardTasks(boardID);
        }
    }

    /**
     * Requests the list of boards. Should be responded to with SETPROJECTLIST
     * GETPROJECTLIST -> SETPROJECTLIST
     * @throws IOException
     */
    public void getBoards() throws IOException {
        // Send the request
        connection.sendMessage(null, MessageType.GETPROJECTLIST);

        // Wait for the response
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.SETPROJECTLIST){
            // Handle error
            throw new InvalidResponseException("Expected SETPROJECTLIST, received " + responseType.name());
        }
    }

    /**
     * Board creation request. Should be responded to with RESPONSE
     * SETPROJECT -> RESPONSE
     * @param id
     * @param name
     * @throws IOException
     */
    public void createBoard(long id, String name) throws IOException {
        // Send the request
        connection.sendMessage(new RawProject(id, new RawTask[]{}, name, "URL"), MessageType.SETPROJECT);

        // Wait for the response
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.RESPONSE){
            // Handle error
            throw new InvalidResponseException("Expected RESPONSE, received " + responseType.name());
        }
    }

    /**
     * Login request. Should be responded to with RESPONSE
     * SETPROJECT -> RESPONSE
     * @param username
     * @param password
     * @throws IOException
     */
    public void login(String username, String password) throws IOException {
        // Send the request
        connection.sendMessage(new RawLogin(username, password), MessageType.LOGIN);

        // Wait for the response
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.LOGIN){
            throw new InvalidResponseException("Expected LOGIN, received " + responseType.name());
        }
    }

    public void addEmail(String email) throws IOException {
        RawUser rawUser = new RawUser(
                0,
                null,
                null,
                email,
                null,
                null,
                null,
                null
        );

        connection.sendMessage(rawUser, MessageType.USERINFO);
        // Wait for the response
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.RESPONSE){
            throw new InvalidResponseException("Expected RESPONSE, received " + responseType.name());
        }
    }

    public void setProjectPermission(long projectID, Permissions permission, String username) throws IOException {
        RawUser rawUser = new RawUser(
                0,
                username,
                null,
                null,
                null,
                new long[]{projectID},
                new int[]{permission.getIndex()},
                null
        );

        connection.sendMessage(rawUser, MessageType.USERINFO);
        // Wait for the response
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.RESPONSE){
            throw new InvalidResponseException("Expected RESPONSE, received " + responseType.name());
        }
    }
    /*
    public void addFriend(String friendUsername) throws IOException {
        RawUser rawUser = new RawUser(
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                new long[]{1}
        );

        connection.sendMessage(rawUser, MessageType.USERINFO);
        // Wait for the response
        MessageType responseType = waitForResponse();
        if(responseType != MessageType.RESPONSE){
            throw new InvalidResponseException("Expected RESPONSE, received " + responseType.name());
        }
    }*/
}
