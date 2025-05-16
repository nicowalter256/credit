package com.metropol.credit.configurations;

import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class WebAppConfiguration {

        @Autowired
        ConfigProperties configProperties;

        @Bean
        @Scope("prototype")
        public Logger generateLogger(InjectionPoint injectionPoint) {
                Class<?> autoWiredClass = injectionPoint.getMember().getDeclaringClass();
                return LoggerFactory.getLogger(autoWiredClass);
        }

        String generatePatternString(String[] keys) {
                StringBuilder patternBuilder = new StringBuilder("(\"(");
                for (int i = 0; i < keys.length; i++) {
                        String key = keys[i];
                        StringBuilder flexibleKey = new StringBuilder();
                        for (char c : key.toCharArray()) {
                                if (c == '_') {
                                        flexibleKey.append("_?");
                                } else {
                                        flexibleKey.append(c);
                                }
                        }
                        patternBuilder.append(flexibleKey);
                        if (i < keys.length - 1) {
                                patternBuilder.append("|");
                        }
                }
                patternBuilder.append(")\"\\s*:\\s*\"?)([^\\s\",}]+)");
                return patternBuilder.toString();
        }

        @Bean
        public Pattern generateMaskPattern() {
                String[] SENSITIVE_KEYS = configProperties.getSensitiveKeys().split(",");

                final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile(
                                generatePatternString(SENSITIVE_KEYS), Pattern.CASE_INSENSITIVE);
                return SENSITIVE_DATA_PATTERN;
        }

        @Bean
        public ModelMapper modelMapper() {
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD)
                                .setSkipNullEnabled(true);
                return modelMapper;
        }

}
