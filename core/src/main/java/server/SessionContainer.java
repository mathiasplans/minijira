package server;

import auth.SecurityHelper;
import common.UserContainer;
import data.RawLogin;
import messages.Session;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionContainer {
    private final List<Session> sessions;
    private int idCounter = 0;
    private final UserContainer users;

    public SessionContainer(UserContainer container){
        sessions = new ArrayList<>();
        users = container;
    }

    public Session getSession(byte[] key){
        for(Session session: sessions){
            if(Arrays.equals(session.getSessionKey(), key))
                return session;
        }

        return null;
    }

    public Session createSession(RawLogin login, Socket connection){
        Session out = new Session(
                SecurityHelper.generateSessionKey(),
                idCounter++,
                users.getByName(login.username).getId(),
                connection.getInetAddress().getHostAddress(),
                connection.getPort(),
                connection.getLocalAddress().getHostAddress(),
                connection.getLocalPort()
        );

        sessions.add(out);

        return out;
    }
}
