package common;

import data.RawUser;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class which defines the user
 */
public class User {
    private final String name;
    private String email;
    private final long id;
    private long lastOnline;
    private final Set<Long> friends = new HashSet<>();
    private final Map<Long, Permissions> permissions = new HashMap<>();
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

        this.hash = hash;
        this.salt = salt;
    }

    public User(@NotNull RawUser user){
        name = user.username;
        email = user.userEmail;
        id = user.userId;
        hash = user.passwordHash;
        salt = null;

        // Fill the projects and permissions
        for(int i = 0; i < user.projectRights.length; i++){
            permissions.put(user.projects[i], Permissions.getPermission(user.projectRights[i]));
        }

        // Fill the friends
        for(long friendID: user.friendList){
            friends.add(friendID);
        }
    }

    public RawUser getRawUser(){
        RawUser out = new RawUser(
                id,
                name,
                hash,
                email,
                lastOnline,
                null,
                null,
                null
        );

        // Fill the projects and permissions
        out.projects = new long[permissions.size()];
        out.projectRights = new int[permissions.size()];

        Set<Long> keys = permissions.keySet();
        int index = 0;
        for(long projectID: keys){
            out.projects[index] = projectID;
            out.projectRights[index] = permissions.get(projectID).getIndex();
            index++;
        }

        // Fill the friends
        out.friendList = new long[friends.size()];
        List<Long> friendList = new ArrayList<>(friends);

        for (int i = 0; i < out.friendList.length; i++) {
            out.friendList[i] = friendList.get(i);
        }

        return out;
    }

    /**
     * Get the name of the user
     * @return name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Get the ID of the user
     * @return ID of the user
     */
    public long getId() {
        return id;
    }

    /**
     * Get the permissions to all the projects
     * @return Map of the permissions, where the key is the project ID and the value is Permissions type enum
     */
    public Map<Long, Permissions> getPermissions() {
        return permissions;
    }
}
