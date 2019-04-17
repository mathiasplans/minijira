package server;

import common.Boards;
import common.Task;
import common.TaskContainer;
import common.UserContainer;
import data.*;
import messages.JiraMessageHandler;
import messages.MessageType;
import messages.ProtocolConnection;

import java.io.IOException;

public class ServerMessage implements JiraMessageHandler {
    private final TaskContainer tasks;
    private final UserContainer users;
    private final Boards boards;
    private ProtocolConnection connection;

    /**
     * Main constructor. Initializes task and user containers
     * @param tasks
     * @param users
     */
    public ServerMessage(TaskContainer tasks, UserContainer users, Boards boards) {
        this.tasks = tasks;
        this.users = users;
        this.boards = boards;
    }

    public void setConnection(ProtocolConnection connection){
        this.connection = connection;
    }

    @Override
    public RawError createTask(RawTask newTask) {
        // TODO: Check user auth
        // Creates a new task and stores it into the container
        tasks.newTask(newTask);
        return null;
    }

    @Override
    public RawError removeTask(Long taskId) {
        // TODO: Check user auth
        // Removes a task by it's id
        tasks.removeTask(taskId);
        return null;
    }

    @Override
    public RawError updateTask(RawTask updatedTask) {
        // TODO: Check user auth
        // Update a task
        tasks.updateTask(updatedTask);
        return null;
    }

    @Override
    public RawError getServerTaskList(Object unimplemented) {
        for(Task task: tasks.getTasks()){
            try{
                connection.sendMessage(task.getRawTask(), MessageType.UPDATETASK);
            }catch (IOException e){
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
        for(Task task: tasks.getTasks(projectId)){
            try{
                connection.sendMessage(boards.getRawProject(projectId), MessageType.SETPROJECT);
            }catch (IOException e){
                System.out.println("Failed to send message: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public RawError setProject(RawProject rawProject) {
        return null;
    }
}
