package client;

import common.Boards;
import common.TaskContainer;
import common.UserContainer;
import data.*;
import messages.JiraMessageHandler;

import java.io.IOException;

/**
 * Communication handler. What the client does when it receives a type of a message
 */
public class ClientMessage implements JiraMessageHandler {
    private final TaskContainer tasks;
    private final UserContainer users;
    private final Boards boards;
    private final ClientAuth auth;

    public ClientMessage(TaskContainer tasks, UserContainer users, Boards boards, ClientAuth auth) {
        this.tasks = tasks;
        this.users = users;
        this.boards = boards;
        this.auth = auth;
    }

    @Override
    public RawError createTask(RawTask newTask) {
        tasks.newTask(newTask);
        return null;
    }

    @Override
    public RawError removeTask(Long taskId) {
        tasks.removeTask(taskId);
        return null;
    }

    @Override
    public RawError updateTask(RawTask updatedTask) {
        tasks.updateTask(updatedTask);
        return null;
    }

    @Override
    public RawError getServerTaskList(Object unimplemented) {
        return null;
    }

    @Override
    public RawError setSession(RawSession session) {
        return null;
    }

    @Override
    public RawError login(RawLogin rawLogin){
        switch(rawLogin.username){
            case "exists":
                auth.loginRequest();
            break;

            case "does not exist":
                auth.doesNotExist();
            break;

            case "logged in":
                auth.loginConfirmed();
            break;

            case "wrong password":
                auth.wrongPassword();
            break;

            case "cancelled":
                auth.loginCancelled();
            break;

            case "logged out":
                auth.loggedOut();
            break;

            case "registered":
                auth.registrationSuccessful();
            break;

            case "already exists":
                auth.alreadyExists();
            break;

            default:
                throw new InvalidResponseException("Unknown LOGIN message recieved from Server");
        }

        return null;
    }

    @Override
    public RawError userInfo(RawUser user) {
        return null;
    }

    @Override
    public RawError getProjectList() {
        return null;
    }

    @Override
    public RawError setProjectList(RawProjectNameList projectNames) {
        boards.registerBoard(projectNames);
        return null;
    }

    @Override
    public RawError getProject(Long projectId) {
        return null;
    }

    @Override
    public RawError setProject(RawProject rawProject) {
        boards.registerBoard(rawProject);
        return null;
    }
}
