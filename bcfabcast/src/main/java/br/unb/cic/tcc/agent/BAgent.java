package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Set;

public interface BAgent {

    String RSA = "RSA";
    String SHA_256 = "SHA-256";

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

    default BProtocolMessage createAssignedMessage(ProtocolMessage message, Set<ProtocolMessage> proofs, KeyPair keyPair){
        String valueString = ""+message.getAgentSend()
                +message.getProtocolMessageType().toString()
                +message.getRound()
                +message.getMessage();

        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] hash = digest.digest(valueString.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
            byte[] hashCalculado = cipher.doFinal(hash);

            return new BProtocolMessage(message, hashCalculado, proofs, keyPair.getPublic());
        } catch (Exception e){
            throw new RuntimeException("Erro ao assinar msg", e);
        }
    }

    default boolean verifyMsg(BProtocolMessage message){
        try {
            Cipher instance = Cipher.getInstance(RSA);
            instance.init(Cipher.DECRYPT_MODE, message.getPublicKey());
            byte[] decriptValue = instance.doFinal(message.getAssinatura());
            String hashEncontrado = new String(decriptValue);

            String hashLocal = ""+message.getAgentSend()
                    +message.getProtocolMessageType().toString()
                    +message.getRound()
                    +message.getMessage();

            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] hashDigest = digest.digest(hashLocal.getBytes(StandardCharsets.UTF_8));

            String hashCalculado = new String(hashDigest);

            return hashCalculado.equals(hashEncontrado);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possivel verificar a msg", e);
        }
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
