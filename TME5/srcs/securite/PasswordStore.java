package srcs.securite;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PasswordStore {

    private final String hashAlgorithm;
    private final Map<String, byte[]> passwords;

    public PasswordStore(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
        passwords = new HashMap<>();
    }

    public void storePassword(String user, String passwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
        md.update(passwd.getBytes());
        passwords.put(user, md.digest());
    }

    public boolean checkPassword(String user, String passwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
        md.update(passwd.getBytes());
        return Arrays.equals(md.digest(), passwords.get(user));
    }

}
