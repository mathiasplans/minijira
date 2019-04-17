package client;

import common.TaskContainer;
import common.UserContainer;
import data.*;
import messages.JiraMessageHandler;

/**
 * Communication handler. What the client does when it receives a type of a message
 */
public class ClientMessage implements JiraMessageHandler {
    private final TaskContainer tasks;
    private final UserContainer users;

    public ClientMessage(TaskContainer tasks, UserContainer users) {
        this.tasks = tasks;
        this.users = users;
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
    public RawError login(RawLogin rawLogin) {
        return null;
    }

    @Override
    public RawError getProjectList() {
        return null;
    }

    @Override
    public RawError setProjectList(RawProjectNameList projectNames) {
        return null;
    }

    @Override
    public RawError getProject(Long projectId) {
        return null;
    }

    @Override
    public RawError setProject(RawProject rawProject) {
        for(RawTask task: rawProject.tasks){
            tasks.updateTask(task);
        }
        return null;
    }
}
