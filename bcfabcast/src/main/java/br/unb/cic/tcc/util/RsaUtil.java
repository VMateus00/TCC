package br.unb.cic.tcc.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RsaUtil {

    private static RsaUtil rsaUtil;
    private KeyPairGenerator rsa;

    private RsaUtil() {
        try {
            rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static KeyPair generateKeyPair(){
        if(rsaUtil == null){
            rsaUtil = new RsaUtil();
        }
        return rsaUtil.getRsa().generateKeyPair();
    }

    private KeyPairGenerator getRsa() {
        return rsa;
    }
}
