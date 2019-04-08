package server;

import messages.JiraMessageHandler;
import messages.messagetypes.*;

public class ServerMessage implements JiraMessageHandler {
    @Override
    public ErrorMessage createTask(CreateTaskMessage message) {
        return null;
    }

    @Override
    public ErrorMessage removeTask(RemoveTaskMessage message) {
        return null;
    }

    @Override
    public ErrorMessage updateTask(UpdateTaskMessage message) {
        return null;
    }

    @Override
    public ErrorMessage getServerTaskList(GetServerTaskListMessage message) {
        return null;
    }

    @Override
    public ErrorMessage setSession(SetSessionMessage session) {
        return null;
    }

    @Override
    public ErrorMessage login(LoginMessage message) {
        return null;
    }

    @Override
    public ErrorMessage getProjectList(GetProjectListMessage message) {
        return null;
    }

    @Override
    public ErrorMessage setProjectList(SetProjectListMessage message) {
        return null;
    }

    @Override
    public ErrorMessage getProject(GetProjectMessage message) {
        return null;
    }

    @Override
    public ErrorMessage setProject(SetProjectMessage message) {
        return null;
    }
}
