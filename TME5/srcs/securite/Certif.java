package srcs.securite;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;

public class Certif implements Serializable {

    private final String identifier;
    private final PublicKey publicKey;
    private final byte[] signature;
    private final String algorithm;

    protected Certif(String owner_id, PublicKey public_key, byte[] signature, String algorithm) {
        this.identifier = owner_id;
        this.publicKey = public_key;
        this.signature = signature;
        this.algorithm = algorithm;
    }

    public String getIdentifier() {
        return identifier;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getAuthoritySignature() {
        return signature;
    }

    public boolean verify(PublicKey publickeyauthority) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publickeyauthority);
        signature.update(publicKey.getEncoded());
        return signature.verify(getAuthoritySignature());
    }

    public byte[] getEncoded() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Certif getDecoded(byte[] encoded) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(encoded))) {
            return (Certif) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
