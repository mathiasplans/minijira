package server;

import auth.SecurityHelper;
import common.UserContainer;
import data.RawLogin;
import messages.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionContainer {
    private final List<Session> sessions;
    private int idCounter = 0;
    private final UserContainer users;

    public SessionContainer(UserContainer container){
        sessions = new ArrayList<>();
        users = container;
    }

    public Session getSession(long id){
        for(Session session: sessions){
            if(session.getSessionid() == id)
                return session;
        }

        return null;
    }

    public Session createSession(RawLogin login){
        Session out = new Session(
                SecurityHelper.generateSessionKey(),
                idCounter++,
                users.
        )
    }
}
