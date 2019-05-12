package common;

import data.RawUser;
import org.jetbrains.annotations.NotNull;

import java.security.Permission;
import java.util.*;

/**
 * Class which defines the user
 */
public class User {
    private final String name;
    private String email;
    private final long id;
    private long lastOnline;
    private final List<Long> friends = new ArrayList<>();
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

        for (int i = 0; i < out.friendList.length; i++) {
            out.friendList[i] = friends.get(i);
        }

        return out;
    }

    public boolean hasRights(long project, Permissions level){
        return (permissions.get(project).compareTo(level) >= 0);
    }

    public void setProjectRights(long projectID, Permissions level){
        if(permissions.containsKey(projectID)){
            permissions.replace(projectID, level);
        }else{
            permissions.put(projectID, level);
        }
    }

    public void addFriend(long userID){
        if(!friends.contains(userID)){
            friends.add(userID);
        }
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
     * Get the stored password hash and salt of the user
     * @return password hash and salt of the user
     */
    public byte[] getHashAndSalt() {
        byte[] output =  Arrays.copyOf(hash, 64);
        System.arraycopy(salt, 0, output, 32, 32);

        return output;
    }

    /**
     * Set the time of last login to current time
     */
    public void setLastOnline() {
        lastOnline = System.currentTimeMillis();
    }

    /**
     * Set the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the permissions to all the projects
     * @return Map of the permissions, where the key is the project ID and the value is Permissions type enum
     */
    public Map<Long, Permissions> getPermissions() {
        return permissions;
    }
}
