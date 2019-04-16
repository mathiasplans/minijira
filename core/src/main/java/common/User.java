package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which defines the user
 */
public class User {
    private String name;
    private final long id;
    private final List<Long> projects;
    private final Map<Long, Permissions> permissions;
    private final byte[] hash;
    private final byte[] salt;

    /**
     * Primary constructor. Initializes the name, ID and authentication data
     * of the user.
     * @param name name of the user
     * @param id id of the user
     * @param hash password hash for the user
     * @param salt password encryption/decryption salt for the user
     */
    public User(String name, long id, byte[] hash, byte[] salt) {
        this.name = name;
        this.id = id;

        projects = new ArrayList<>();
        permissions = new HashMap<>();

        this.hash = hash;
        this.salt = salt;
    }

    /**
     * Get the name of the user
     * @return name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the user
     * @param name name to be assigned to the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the ID of the user
     * @return ID of the user
     */
    public long getId() {
        return id;
    }

    /**
     * Get the projects where this user participates
     * @return List of the projects
     */
    public List<Long> getProjects() {
        return projects;
    }

    /**
     * Get the permissions to all the projects
     * @return Map of the permissions, where the key is the project ID and the value is Permissions type enum
     */
    public Map<Long, Permissions> getPermissions() {
        return permissions;
    }
}
