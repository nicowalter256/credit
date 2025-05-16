package com.metropol.credit.models;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import lombok.Data;

@Data
public class PemKeyStore {
    RSAPublicKey rsaPublicKey;
    RSAPrivateKey rsaPrivateKey;

    public PemKeyStore(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

}
