package com.metropol.credit.interfaces;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.metropol.credit.models.AccessToken;

public interface AuthenticationService {

        AccessToken createToken(Long userId, String accountType, String role)
                        throws JWTCreationException;

        DecodedJWT verifyToken(String token) throws JWTVerificationException;

        String encrypt(String data) throws Exception;

        String decrypt(String data) throws Exception;

        String getHashedPassword(String password, String savedSalt) throws NoSuchAlgorithmException;

        Map<String, String> generateUserPassword(String password) throws NoSuchAlgorithmException;

}
