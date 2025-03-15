package srcs.securite;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.GeneralSecurityException;

public class SecureChannelConfidentiality extends ChannelDecorator {

    private final Channel channel;
    private final Authentication auth;
    private final SecretKey secretKey;

    public SecureChannelConfidentiality(Channel channel, Authentication auth, String algorithm, int keySize) throws GeneralSecurityException, IOException, ClassNotFoundException {
        super(channel);
        this.channel = channel;
        this.auth = auth;

        // Generating local symmetric key
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(keySize);
        SecretKey localSecretKey = keyGen.generateKey();

        Cipher cipher = Cipher.getInstance(auth.getLocalKeys().getPublic().getAlgorithm());

        // Sending ciphered local symmetric key
        cipher.init(Cipher.ENCRYPT_MODE, auth.getRemoteCertif().getPublicKey());
        SealedObject localSealedKey = new SealedObject(localSecretKey, cipher);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(localSealedKey);
        channel.send(bos.toByteArray());

        // Receiving remote symmetric key
        cipher.init(Cipher.DECRYPT_MODE, auth.getLocalKeys().getPrivate());
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(channel.recv()));
        SecretKey remoteSecretKey = (SecretKey) ((SealedObject) ois.readObject()).getObject(cipher);

        // Keeping the superior symmetric key (comparing their hash)
        if (localSecretKey.hashCode() < remoteSecretKey.hashCode())
            secretKey = remoteSecretKey;
        else
            secretKey = localSecretKey;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public void send(byte[] bytesArray) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            channel.send(cipher.doFinal(bytesArray));
        } catch (GeneralSecurityException e) {
            throw new CorruptedMessageException("An error occurred on the connection", e);
        }
    }

    @Override
    public byte[] recv() throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(channel.recv());
        } catch (GeneralSecurityException e) {
            throw new CorruptedMessageException("An error occurred on the connection", e);
        }
    }

}
