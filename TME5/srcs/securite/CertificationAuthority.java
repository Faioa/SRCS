package srcs.securite;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

public class CertificationAuthority {

    private final String signAlgorithm;
    private final KeyPair keyPair;
    private Map<String, Certif> certificates;

    public CertificationAuthority(String cipherAlgorithm, int sizekey, String signAlgorithm) throws NoSuchAlgorithmException {
        Util.generateNewKeyPair(cipherAlgorithm, sizekey);
        this.signAlgorithm = signAlgorithm;
        keyPair = Util.generateNewKeyPair(cipherAlgorithm, sizekey);
        certificates = new HashMap<>();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public Certif getCertificate(String identifier) {
        return certificates.get(identifier);
    }

    public Certif declarePublicKey(String identifier, PublicKey pubk) throws GeneralSecurityException {
        if (certificates.containsKey(identifier))
            throw new GeneralSecurityException("Certificate with identifier " + identifier + " already exists");

        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initSign(keyPair.getPrivate());
        signature.update(pubk.getEncoded());
        Certif certif = new Certif(identifier, pubk, signature.sign(), signAlgorithm);
        certificates.put(identifier, certif);
        return certif;
    }

}
