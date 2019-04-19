package client;

import common.Task;
import messages.MessageType;
import messages.ProtocolConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.Duration;

public class Sync {
    private final ProtocolConnection connection;
    private final DataInputStream in;

    public Sync(ProtocolConnection connection, DataInputStream in){
        this.connection = connection;
        this.in = in;
    }


    // TODO: handle errors also!

    public void updateTask(Task task) throws IOException {
        connection.sendMessage(task.getRawTask(), MessageType.UPDATETASK);
    }

    public void createTask(Task task) throws IOException {
        // Send the message
        connection.sendMessage(task.getRawTask(), MessageType.CREATETASK);
    }

    public void getTasks() throws IOException {
        // Send the message
        connection.sendMessage(null, MessageType.GETSERVERTASKLIST);

        // Receive the tasks
        while (true){
//            if(in.available())
        }
    }
}
