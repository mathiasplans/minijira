package server;

import common.UserContainer;
import data.RawUser;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class ServerAuth{
    UserContainer userContainer;

    public ServerAuth(UserContainer userContainer){
        this.userContainer = userContainer;
    }

    public void registerUser(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        SecureRandom rng = new SecureRandom();
        byte[] salt = new byte[32];
        rng.nextBytes(salt);

        userContainer.newUser(username, generateHash(password, salt), salt);
    }

    public byte[] generateHash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1_000_000, 256);
        byte[] key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        return key;
    }

    public boolean verifyPassword(String password, byte[] salt, byte[] hash) throws NoSuchAlgorithmException, InvalidKeySpecException{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1_000_000, 256);
        byte[] key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();

        return hash.equals(key);
    }
}
