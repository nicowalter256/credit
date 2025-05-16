package com.metropol.credit.configurations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.metropol.credit.models.PemKeyStore;

@Component
public class KeyConfigurations {

    private PEMParser pemParser;
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    ConfigProperties configProperties;

    @Bean
    public PemKeyStore gPemKeyStore() throws Exception {

        File filePrivateKey = null;
        File filePublicKey = null;

        filePrivateKey = resourceLoader.getResource(configProperties.localPrivKeyPath).getFile();
        filePublicKey = resourceLoader.getResource(configProperties.localPubKeyPath).getFile();

        if (filePrivateKey != null && filePublicKey != null) {

            try {

                if (filePrivateKey.exists() && filePublicKey.exists()) {
                    RSAPrivateKey rsaPrivateKey = readPrivateKey(filePrivateKey);
                    RSAPublicKey rsaPublicKey = readPublicKey(filePublicKey);
                    return new PemKeyStore(rsaPublicKey, rsaPrivateKey);

                }
                return null;
            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }
        }

        return null;

    }

    public RSAPrivateKey readPrivateKey(File file) throws Exception {

        FileReader fileReader = new FileReader(file);
        pemParser = new PEMParser(fileReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
        return (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);

    }

    public RSAPublicKey readPublicKey(File file) throws Exception {
        FileReader fileReader = new FileReader(file);
        pemParser = new PEMParser(fileReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
        return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);

    }

}
