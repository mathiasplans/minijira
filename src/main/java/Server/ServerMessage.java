package Server;

import messages.JiraMessageHandler;

public class ServerMessage implements JiraMessageHandler {
    @Override
    public boolean createTask(byte[] data) {
        return false;
    }

    @Override
    public boolean removeTask(byte[] data) {
        return false;
    }

    @Override
    public boolean updateTimeTask(byte[] data) {
        return false;
    }

    @Override
    public boolean setStatusTask(byte[] data) {
        return false;
    }
}
