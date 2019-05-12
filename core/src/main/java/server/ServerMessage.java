package server;

import common.*;
import data.*;
import messages.JiraMessageHandler;
import messages.MessageType;
import messages.ProtocolConnection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ServerMessage implements JiraMessageHandler {
    private final TaskContainer tasks;
    private final UserContainer users;
    private final Boards boards;
    private final Order orderer;
    private ProtocolConnection connection;
    private User currentUser;
    private User potentialUser;

    /**
     * Main constructor. Initializes task and user containers
     * @param tasks
     * @param users
     */
    @Contract(pure = true)
    public ServerMessage(TaskContainer tasks, UserContainer users, Boards boards, Order orderer, User currentUser) {
        this.tasks = tasks;
        this.users = users;
        this.boards = boards;
        this.orderer = orderer;
        this.currentUser = currentUser;
    }

    public void setConnection(ProtocolConnection connection){
        this.connection = connection;
    }

    private void sendResponse(Object o, MessageType type) {
        try {
            connection.sendMessage(o, type);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void respnd(){
        sendResponse(null, MessageType.RESPONSE);
    }

    private void error(Exception e){
        sendResponse(new RawError("Failed to send the message\n" + e.getMessage()), MessageType.ERROR);
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
                error(e);
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
    public RawError login(RawLogin rawLogin){
        try{
            if(rawLogin.username == null){

                if(rawLogin.password == null){
                    // null, null - cancel login/registration request / logout
                    if(currentUser == null){
                        potentialUser = null;
                        connection.sendMessage(new RawLogin("cancelled", null), MessageType.LOGIN);
                    }else{
                        currentUser = null;
                        potentialUser = null;
                        connection.sendMessage(new RawLogin("logged out", null), MessageType.LOGIN);
                    }

                }else{
                    // null, password - attempts to log in as user previuosly sent via username, null
                    try{
                        if(ServerAuth.logUserIn(potentialUser.getName(), rawLogin.password, users)){
                            currentUser = potentialUser;
                            potentialUser = null;
                            connection.sendMessage(new RawLogin("logged in", null), MessageType.LOGIN);
                        }else{
                            potentialUser = null;
                            connection.sendMessage(new RawLogin("wrong password", null), MessageType.LOGIN);
                        }
                    }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
                        error(e);
                        System.out.println("Failed to log user in: " + e.getMessage());
                    }
                }

            }else{

                if(rawLogin.password == null){
                    // username, null - checks if user exists on the server
                    potentialUser = users.getUser(rawLogin.username);
                    if(potentialUser != null){
                        connection.sendMessage(new RawLogin("exists", null), MessageType.LOGIN);
                    }else{
                        connection.sendMessage(new RawLogin("does not exist", null), MessageType.LOGIN);
                    }

                }else{
                    // username, password - initiates registration procedure
                    if(users.getUser(rawLogin.username) == null){
                        try{
                            currentUser = ServerAuth.registerUser(rawLogin.username, rawLogin.password, users);
                            connection.sendMessage(new RawLogin("registered", null), MessageType.LOGIN);
                        }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
                            error(e);
                            System.out.println("Failed to register user: " + e.getMessage());
                        }
                    }else{
                        connection.sendMessage(new RawLogin("already exists", null), MessageType.LOGIN);
                    }
                }
            }
        }catch (IOException e){
            error(e);
            System.out.println("Failed to send message: " + e.getMessage());
        }
        return null;
    }

    @Override
    public RawError userInfo(RawUser user) {
        ServerAuth.addUserData(currentUser, user, users);
        respnd();
        return null;
    }

    @Override
    public RawError getProjectList() {
        try {
            connection.sendMessage(boards.getRawProjectNameList(), MessageType.SETPROJECTLIST);
        }catch (IOException e){
            error(e);
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
            error(e);
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
