package client;

import common.Task;
import messages.MessageType;
import messages.ProtocolConnection;

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
        if(waitForResponse() != MessageType.RESPONSE){
            // Handle Error
        }
    }

    /**
     * Task creation request. Should be responded to with UPDATETASK (Gets ID from server)
     * CREATETASK -> UPDATETASK
     * @param task
     * @throws IOException
     */
    public void createTask(Task task) throws IOException {
        // Send the message
        connection.sendMessage(task.getRawTask(), MessageType.CREATETASK);

        // Wait for UPDATETASK type message
        if(waitForResponse() != MessageType.UPDATETASK){
            // Handle error
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
        if(waitForResponse() != MessageType.SETPROJECT){
            // Handle error
        }
    }

    /**
     * Gets the tasks of specified boards (given as the argument).
     * @param boards
     * @throws IOException
     */
    public void getTasks(Set<Long> boards) throws IOException {
        // For every board, send the query
        // TODO: this is costly, tasks can be in multiple boards
        for(Long boardID: boards){
            getBoardTasks(boardID);
        }
    }
}
