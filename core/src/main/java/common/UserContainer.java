package common;

import com.google.gson.Gson;
import data.RawUser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for containing Users.
 */
public class UserContainer {
    private final List<User> users;
    private static final Gson gson = new Gson();
    private Path inpath;
    private long order = 0;
    private final ContainerHelper<User> container;

    /**
     * Default constructor. Initializes the array of users.
     */
    public UserContainer(){
        users = new ArrayList<>();
        container = new ContainerHelper<>(users);
    }

    /**
     * Secondary constructor. Initializes the array of users with
     * the given list.
     * @param users
     */
    public UserContainer(List<User> users){
        this.users = users;
        container = new ContainerHelper<>(users);
    }

    /**
     * Secondary constructor. Initializes the array of the users with
     * contents of file/directory pointed at by path
     * @param path path to the directory/file where users are stored
     * @throws IOException If file IO fails
     */
    public UserContainer(String path) throws IOException {
        users = new ArrayList<>();
        container = new ContainerHelper<>(users);
        importUsers(path);
    }

    /**
     * Secondary constructor. Initializes the array of the users with
     * contents of file/directory pointed at by path
     * @param path path to the directory/file where users are stored
     * @throws IOException If file IO fails
     */
    public UserContainer(@NotNull Path path) throws IOException {
        users = new ArrayList<>();
        container = new ContainerHelper<>(users);
        importUsers(path);
    }

    /**
     * Imports thasks from given paths. The path
     * has to point to a directory/file. The tasks have to be in JSON
     * format.
     * @param path Path to the directory/file where the tasks are stored
     * @throws IOException If file IO fails
     */
    private void importUsers(String path) throws IOException {
        importUsers(Paths.get(path));
    }

    /**
     * Imports thasks from given paths. The path
     * has to point to a directory/file. The tasks have to be in JSON
     * format.
     * @param path Path to the directory/file where the tasks are stored
     * @throws IOException If file IO fails
     */
    private void importUsers(Path path) throws IOException {
        /* Fill the list */
        container.importItems(path, json -> new User(gson.fromJson(json, RawUser.class)));

        /* Determine the order */
        // Get the biggest ID
        long biggestUserId = -1;
        for(User user: users){
            if(user.getId() > biggestUserId)
                biggestUserId = user.getId();
        }

        // Set new order. This ensures that old Task IDs don't get overwritten
        order = biggestUserId + 1;

        // Set the save path
        inpath = path;
    }

    /**
     * Export stored users into a directory, specified by the path
     * @param path path to the save directory
     * @throws IOException If file IO fails
     */
    public void saveUsers(Path path) throws IOException {
        container.exportItems(path, user -> gson.toJson(user, RawUser.class));
    }

    /**
     * Export stored tasks into directory. This variant can be called only if
     * users were originally imported. The users are saved into the directory whence
     * they were imported.
     * @throws IOException If file IO fails
     * @throws IllegalStateException If the users weren't originally imported.
     *                               UserContainer doesn't know the path! Call saveUsers(String path) variant instead
     */
    public void saveUsers() throws IOException {
        if(inpath == null)
            throw new IllegalStateException(
                    "Can not save tasks: path to the save directory does not exist. Call saveUsers(String path) variant instead"
            );

        saveUsers(inpath);
    }

    /**
     * Method for adding a new user to the container
     * @param newUser user to be added
     */
    public void addUser(User newUser) {
        // Check if user already exists!
        User testUser = getUser(newUser.getId());
        if(testUser != null)
            throw new IllegalArgumentException("User with given ID already exists");

        users.add(newUser);
    }

    /**
     * Method for creating a new user into the container
     * @param name name of the new user
     * @return the created user
     */
    public User newUser(String name){
        User newUser = new User(name, order++, null, null);
        users.add(newUser);
        return newUser;
    }

    /**
     * Method for creating a new user into the container
     * @param name name of the new user
     * @param hash password hash of the new user
     * @param salt password hash salt of the new user
     * @return the created user
     */
    public User newUser(String name, byte[] hash, byte[] salt){
        User newUser = new User(name, order++, hash, salt);
        users.add(newUser);
        return newUser;
    }

    /**
     * Method for creating a new user into the container
     * @param user RawUser object
     * @return the created user
     */
    public User newUser(RawUser user){
        User newUser = new User(user);
        users.add(newUser);
        return newUser;
    }

    /**
     * Acquire the user with specific ID
     * @param id search key
     * @return the user with given ID. Is null if not found
     */
    public User getUser(long id){
        for(User user: users){
            if(user.getId() == id)
                return user;
        }

        return null;
    }

    /**
     * Acquire the user with specific name
     * @param name search key
     * @return the user with given name. Is null if not found
     */
    public User getUser(String name){
        for(User user: users){
            if(user.getName().equals(name))
                return user;
        }

        return null;
    }
}
