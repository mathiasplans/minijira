package client;

import data.*;
import messages.JiraMessageHandler;

public class ClientMessage implements JiraMessageHandler {
    @Override
    public RawError createTask(RawTask newTask) {
        return null;
    }

    @Override
    public RawError removeTask(Long taskId) {
        return null;
    }

    @Override
    public RawError updateTask(RawTask updatedTask) {
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
        return null;
    }
}
