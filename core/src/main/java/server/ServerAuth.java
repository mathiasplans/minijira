package server;

import common.Permissions;
import common.User;
import common.UserContainer;
import data.RawUser;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class ServerAuth{

    /*public ServerAuth(UserContainer userContainer){

    }*/

    public static byte[] generateHash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100_000, 256);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }

    public static boolean verifyPassword(String password, byte[] salt, byte[] hash) throws NoSuchAlgorithmException, InvalidKeySpecException{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100_000, 256);
        byte[] key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        return Arrays.equals(key, hash);
    }

    public static byte[] generateSalt() {
        // generate the salt
        SecureRandom rng = new SecureRandom();
        byte[] salt = new byte[32];
        rng.nextBytes(salt);
        return salt;
    }

    public static User registerUser(String username, String password, UserContainer userContainer) throws NoSuchAlgorithmException, InvalidKeySpecException{
        byte[] salt = generateSalt();
        userContainer.newUser(username, generateHash(password, salt), salt);
        return userContainer.getUser(username);
    }

    public static boolean logUserIn(String username, String password, UserContainer userContainer) throws NoSuchAlgorithmException, InvalidKeySpecException{
        User user = userContainer.getUser(username);
        byte[] hash = new byte[32];
        System.arraycopy(user.getHashAndSalt(), 0, hash, 0, 32);
        byte[] salt = new byte[32];
        System.arraycopy(user.getHashAndSalt(), 33, salt, 0, 32);
        if(verifyPassword(password, salt, hash)){
            user.setLastOnline();
            return true;
        }else{
            return false;
        }
    }

    public static void addUserData(User currentUser, RawUser data, UserContainer users){
        User affected;
        boolean self;
        if(data.username != null){
            affected = users.getUser(data.username);
            self = false;
        }else{
            affected = currentUser;
            self = true;
        }

        if(data.userEmail != null){
            if(self){
                affected.setEmail(data.userEmail);
            }else{
                throw new NoPermissionException("Can't change emails of other users!");
            }
        }

        if(data.projectRights != null){
            for(int projectIndex = 0; projectIndex < data.projectRights.length; projectIndex++){
                if(currentUser != null && currentUser.hasRights(projectIndex, Permissions.ALL)){
                    affected.setProjectRights(data.projects[projectIndex], Permissions.valueOf(data.projectRights[projectIndex]));
                }else{
                    throw new NoPermissionException("Don't have the rights to change permissions for project " + data.projects[projectIndex]);
                }
            }
        }

        if(data.friendList != null){
            if(self){
                for(long friend:data.friendList){
                    affected.addFriend(friend);
                }
            }else{
                throw new NoPermissionException("Can't change friendlists of other users!");
            }
        }

    }
}
