package com.metropol.credit.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropol.credit.configurations.ConfigProperties;
import com.metropol.credit.interfaces.AuthenticationService;
import com.metropol.credit.models.AccessToken;
import com.metropol.credit.models.PemKeyStore;

@Service
public class AuthenticationImpl implements AuthenticationService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PemKeyStore pemKeyStore;

    @Autowired
    ConfigProperties configs;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public AccessToken createToken(Long accountId, String accountType, String role)
            throws JWTCreationException {
        try {
            // 1 hour
            Instant tokenInstant = Instant.now().plusSeconds(86400);
            // 4 hours
            Instant refreshInstant = Instant.now().plusSeconds(172800);
            Algorithm algorithm = Algorithm.RSA256(pemKeyStore.getRsaPublicKey(), pemKeyStore.getRsaPrivateKey());
            String accessToken = JWT.create().withIssuer("auth").withClaim("accountId", accountId)
                    .withClaim("accountType", accountType).withClaim("role", role)

                    .withExpiresAt(Date.from(tokenInstant))
                    .withIssuedAt(Date.from(Instant.now())).sign(algorithm);

            String refreshToken = JWT.create().withIssuer("auth").withClaim("accountId", accountId)
                    .withClaim("accountType", accountType).withExpiresAt(Date.from(refreshInstant))

                    .withIssuedAt(Date.from(Instant.now())).sign(algorithm);
            AccessToken createdToken = new AccessToken(accessToken, refreshToken,
                    ZonedDateTime.ofInstant(refreshInstant, ZoneOffset.UTC));

            return createdToken;
        } catch (JWTCreationException exception) {

            throw exception;
        }
    }

    @Override
    public String getHashedPassword(String password, String savedSalt) throws NoSuchAlgorithmException {

        byte[] salt = Base64.getDecoder().decode(savedSalt);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(salt);
        byte[] hashed = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashed);
    }

    @Override
    public Map<String, String> generateUserPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        messageDigest.update(salt);
        byte[] hashedPassword = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

        Map<String, String> generated = new HashMap<>();

        generated.put("password", Base64.getEncoder().encodeToString(hashedPassword));
        generated.put("salt", Base64.getEncoder().encodeToString(salt));
        return generated;
    }

    @Override
    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.RSA256(pemKeyStore.getRsaPublicKey(), pemKeyStore.getRsaPrivateKey());
        try {
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth").build();
            return verifier.verify(token);
        } catch (JWTVerificationException ex) {
            throw ex;

        }
    }

    @Override
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pemKeyStore.getRsaPublicKey());
        byte[] bytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public String decrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pemKeyStore.getRsaPrivateKey());
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decrypted);
    }

}
