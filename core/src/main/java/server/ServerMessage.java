package server;

import common.TaskContainer;
import common.UserContainer;
import data.*;
import messages.JiraMessageHandler;

public class ServerMessage implements JiraMessageHandler {
    private final TaskContainer tasks;
    private final UserContainer users;

    /**
     * Main constructor. Initializes task and user containers
     * @param tasks
     * @param users
     */
    public ServerMessage(TaskContainer tasks, UserContainer users) {
        this.tasks = tasks;
        this.users = users;
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
        return null;
    }
}
