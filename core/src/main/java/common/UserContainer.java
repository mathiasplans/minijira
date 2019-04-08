package common;

import java.util.ArrayList;
import java.util.List;

public class UserContainer {
    final private List<User> users;

    public UserContainer(){
        users = new ArrayList<>();
    }

    public UserContainer(List<User> users){
        this.users = users;
    }

    public void addUser(User newUser) throws IllegalArgumentException {
        // Check if user already exists!
        User testUser = getById(newUser.getId());
        if(testUser != null)
            throw new IllegalArgumentException("User with given ID already exists");

        users.add(newUser);
    }

    public User getById(long id){
        for(User user: users){
            if(user.getId() == id)
                return user;
        }

        return null;
    }

    public long


    public String getName(long id){
        return getById(id).getName();
    }
}
