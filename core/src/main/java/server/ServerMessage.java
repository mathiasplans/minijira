package server;

import common.Boards;
import common.Task;
import common.TaskContainer;
import common.UserContainer;
import data.*;
import messages.JiraMessageHandler;
import messages.MessageType;
import messages.ProtocolConnection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ServerMessage implements JiraMessageHandler {
    private final TaskContainer tasks;
    private final UserContainer users;
    private final Boards boards;
    private final Order orderer;
    private ProtocolConnection connection;

    /**
     * Main constructor. Initializes task and user containers
     * @param tasks
     * @param users
     */
    @Contract(pure = true)
    public ServerMessage(TaskContainer tasks, UserContainer users, Boards boards, Order orderer) {
        this.tasks = tasks;
        this.users = users;
        this.boards = boards;
        this.orderer = orderer;
    }

    public void setConnection(ProtocolConnection connection){
        this.connection = connection;
    }

    private void sendResponse(Object o, MessageType type){
        try {
            connection.sendMessage(o, type);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void respnd(){
        sendResponse(null, MessageType.RESPONSE);
    }

    private void error(){
        sendResponse(new RawError("Failed to send the message"), MessageType.ERROR);
    }

    @Override
    public RawError createTask(@NotNull RawTask newTask) {
        // TODO: Check user auth
        // Assign a new ID to the task
        newTask.taskId = orderer.getID();

        // Set a default board
        if(newTask.boards[0] == -1)
            newTask.boards[0] = 0;

        // Creates a new task and stores it into the container
        tasks.newTask(newTask);

        // Send the response
        sendResponse(newTask, MessageType.UPDATETASK);
        return null;
    }

    @Override
    public RawError removeTask(Long taskId) {
        // TODO: Check user auth
        // Removes a task by it's id
        tasks.removeTask(taskId);

        // Send the response
        respnd();
        return null;
    }

    @Override
    public RawError updateTask(RawTask updatedTask) {
        // TODO: Check user auth
        // Update a task
        tasks.updateTask(updatedTask);

        // Send the response
        respnd();
        return null;
    }

    @Override
    public RawError getServerTaskList(Object unimplemented) {
        for(Task task: tasks.getTasks()){
            try{
                connection.sendMessage(task.getRawTask(), MessageType.UPDATETASK);
            }catch (IOException e){
                error();
                System.out.println("Failed to send message: " + e.getMessage());
            }
        }

        return null;
    }

    @Override
    public RawError setSession(RawSession session) {
        return null;
    }

    @Override
    public RawError login(RawLogin rawLogin) {
        // TODO: user auth
        return null;
    }

    @Override
    public RawError getProjectList() {
        try {
            connection.sendMessage(boards.getRawProjectNameList(), MessageType.SETPROJECTLIST);
        }catch (IOException e){
            error();
            System.out.println("Failed to send message: " + e.getMessage());
        }
        return null;
    }

    @Override
    public RawError setProjectList(RawProjectNameList projectNames) {
        return null;
    }

    @Override
    public RawError getProject(Long projectId) {
        try{
            connection.sendMessage(boards.getRawProject(projectId), MessageType.SETPROJECT);
        }catch (IOException e){
            error();
            System.out.println("Failed to send message: " + e.getMessage());
        }
        return null;
    }

    @Override
    public RawError setProject(RawProject rawProject) {
        boards.registerBoard(rawProject);
        respnd();
        return null;
    }
}
