package com.metropol.credit.utilities;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClientHttpResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse originalResponse;
    private byte[] body;

    public ClientHttpResponseWrapper(ClientHttpResponse originalResponse) throws IOException {
        this.originalResponse = originalResponse;
        this.body = readBody(originalResponse.getBody());
    }

    private byte[] readBody(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return new byte[0];
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return originalResponse.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return originalResponse.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return originalResponse.getStatusText();
    }

    @Override
    public HttpHeaders getHeaders() {
        return originalResponse.getHeaders();
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(body);
    }

    @Override
    public void close() {
        originalResponse.close();
    }
}
