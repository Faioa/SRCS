package srcs.securite;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Signature;

public class SecureChannelIntegrity extends ChannelDecorator {

    private final Channel channel;
    private final Authentication auth;
    private final String algorithm;

    public SecureChannelIntegrity(Channel channel, Authentication auth, String signature) {
        super(channel);
        this.channel = channel;
        this.auth = auth;
        this.algorithm = signature;
    }

    @Override
    public void send(byte[] bytesArray) throws IOException {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(auth.getLocalKeys().getPrivate());
            signature.update(bytesArray);
            channel.send(signature.sign());
            channel.send(bytesArray);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("A security exception was thrown", e);
        }
    }

    @Override
    public byte[] recv() throws IOException {
        try {
            byte[] sign_data = channel.recv();
            byte[] data = channel.recv();

            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(auth.getRemoteCertif().getPublicKey());
            signature.update(data);
            if (!signature.verify(sign_data))
                throw new CorruptedMessageException("The integrity of the data is compromised");

            return data;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("A security exception was thrown", e);
        }
    }
}
