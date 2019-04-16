package common;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for containing Users.
 */
public class UserContainer {
    private final List<User> users;

    /**
     * Default constructor. Initializes the array of users.
     */
    public UserContainer(){
        users = new ArrayList<>();
    }

    /**
     * Secondary constructor. Initializes the array of users with
     * the given list.
     * @param users
     */
    public UserContainer(List<User> users){
        this.users = users;
    }

    /**
     * Method for adding a new user to the container
     * @param newUser user to be added
     */
    public void addUser(User newUser) {
        // Check if user already exists!
        User testUser = getById(newUser.getId());
        if(testUser != null)
            throw new IllegalArgumentException("User with given ID already exists");

        users.add(newUser);
    }

    /**
     * Aquire the user with specific ID
     * @param id search key
     * @return the user with given ID. Is null if not found
     */
    public User getById(long id){
        for(User user: users){
            if(user.getId() == id)
                return user;
        }

        return null;
    }

    /**
     * Aquire the user with specific name
     * @param name search key
     * @return the user with given name. Is null if not found
     */
    public User getByName(String name){
        for(User user: users){
            if(user.getName().equals(name))
                return user;
        }

        return null;
    }

    /**
     * Returns the name of the user with specific ID
     * @param id search key
     * @return name of the user with given ID
     */
    public String getName(long id){
        return getById(id).getName();
    }
}
