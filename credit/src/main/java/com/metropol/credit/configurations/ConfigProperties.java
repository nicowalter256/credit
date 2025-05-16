package com.metropol.credit.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@ConfigurationProperties(prefix = "app.configs")
@Configuration
@Data
public class ConfigProperties {
    // Private key path on local machine
    public String localPrivKeyPath;
    // Public key path on local machine
    public String localPubKeyPath;
    public String sensitiveKeys;

}
