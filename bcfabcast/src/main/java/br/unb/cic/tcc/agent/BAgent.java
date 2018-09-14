package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.messages.ProtocolMessageType;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface BAgent {

    String RSA = "RSA";

    default byte[] encrypt(ProtocolMessageType protocolMessageType, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(protocolMessageType.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

    default byte[] decrypt(PublicKey publicKey, byte[] bytesEncriptados){
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(bytesEncriptados);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "".getBytes();
    }

    default boolean decryptAndCompare(PublicKey publicKey, byte[] bytesEncriptados, ProtocolMessageType messageType){
        byte[] decrypt = decrypt(publicKey, bytesEncriptados);
        String messageDecrypted = String.valueOf(decrypt);

        if("".equals(messageDecrypted))
            return false;
        return messageType == ProtocolMessageType.valueOf(messageDecrypted);
    }
}
