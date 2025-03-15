package srcs.securite;

import javax.crypto.Cipher;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Random;

public class Authentication {

    private final Channel channel;
    private final Certif localCertif;
    private final Certif remoteCertif;
    private final KeyPair localKeys;
    private final PublicKey acPublicKey;
    private final PasswordStore passwordStore;
    private final String login;
    private final String password;

    // Server's constructor
    public Authentication(Channel channel, Certif localCertif, KeyPair localKeys, PasswordStore passwordStore, PublicKey acPublicKey) throws IOException, GeneralSecurityException {
        this.channel = channel;
        this.localCertif = localCertif;
        this.localKeys = localKeys;
        this.acPublicKey = acPublicKey;
        this.passwordStore = passwordStore;
        login = null;
        password = null;

        try {
            // Sending own certificate
            channel.send(localCertif.getEncoded());

            // Receiving distant certificate
            remoteCertif = Certif.getDecoded(channel.recv());

            // Verifying distant certificate (supposing both have the same AC)
            if (!remoteCertif.verify(acPublicKey))
                throw new CertificateCorruptedException("Certificate verification failed");

            Cipher cipher = Cipher.getInstance(acPublicKey.getAlgorithm());

            // Receiving and sending the ciphered nonce
            cipher.init(Cipher.ENCRYPT_MODE, localKeys.getPrivate());
            channel.send(cipher.doFinal(channel.recv()));

            // Pick a random nonce
            Random randGen = new Random();
            int nonce = randGen.nextInt();

            // Sending the nonce
            ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
            buf.putInt(nonce);
            buf.rewind();
            channel.send(buf.array());

            // Receiving the distant ciphered nonce
            cipher.init(Cipher.DECRYPT_MODE, remoteCertif.getPublicKey());
            buf.put(cipher.doFinal(channel.recv()));
            buf.rewind();
            int ciphered_nonce = buf.getInt();

            // Authenticating the client
            if (ciphered_nonce != nonce)
                throw new AuthenticationFailedException("Nonce does not match");

            // Receiving login
            String login = new String(channel.recv());

            // Receiving hashed password
            cipher.init(Cipher.DECRYPT_MODE, localKeys.getPrivate());
            String password = new String(cipher.doFinal(channel.recv()));

            // Testing credentials
            if (!passwordStore.checkPassword(login, password))
                throw new AuthenticationFailedException("Credentials do not match");
        } catch (EOFException | SocketException e) {
            throw new AuthenticationFailedException("An error occurred on the connection", e);
        }
    }

    // Client's constructor
    public Authentication(Channel channel, Certif localCertif, KeyPair localKeys, String login, String password, PublicKey acPublicKey) throws IOException, GeneralSecurityException {
        this.channel = channel;
        this.localCertif = localCertif;
        this.localKeys = localKeys;
        this.acPublicKey = acPublicKey;
        this.login = login;
        this.password = password;
        passwordStore = null;

        try {
            // Sending own certificate
            channel.send(localCertif.getEncoded());

            // Receiving distant certificate
            remoteCertif = Certif.getDecoded(channel.recv());

            // Verifying distant certificate (supposing both have the same AC)
            if (!remoteCertif.verify(acPublicKey))
                throw new CertificateCorruptedException("Certificate verification failed");

            Cipher cipher = Cipher.getInstance(acPublicKey.getAlgorithm());

            // Pick a random nonce
            Random randGen = new Random();
            int nonce = randGen.nextInt();

            // Sending the nonce
            ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
            buf.putInt(nonce);
            buf.rewind();
            channel.send(buf.array());

            // Receiving the distant ciphered nonce
            cipher.init(Cipher.DECRYPT_MODE, remoteCertif.getPublicKey());
            buf.put(cipher.doFinal(channel.recv()));
            buf.rewind();
            int ciphered_nonce = buf.getInt();

            // Authenticating the server
            if (ciphered_nonce != nonce)
                throw new AuthenticationFailedException("Nonce does not match");

            // Receiving and sending the distant nonce
            cipher.init(Cipher.ENCRYPT_MODE, localKeys.getPrivate());
            channel.send(cipher.doFinal(channel.recv()));

            // Sending login
            channel.send(login.getBytes());

            // Sending the hashed ciphered password
            cipher.init(Cipher.ENCRYPT_MODE, remoteCertif.getPublicKey());
            channel.send(cipher.doFinal(password.getBytes()));
        } catch (EOFException | SocketException e) {
            throw new AuthenticationFailedException("An error occurred on the connection", e);
        }
    }

    public Certif getLocalCertif() {
        return localCertif;
    }

    public KeyPair getLocalKeys() {
        return localKeys;
    }

    public Certif getRemoteCertif() {
        return remoteCertif;
    }

}
