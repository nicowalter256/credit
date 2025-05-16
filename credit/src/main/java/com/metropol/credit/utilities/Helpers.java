package com.metropol.credit.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class Helpers {

    public static String maskSensitiveData(String data, Pattern pattern) {
        if (!StringUtils.hasText(data)) {
            return null;
        }
        Matcher matcher = pattern.matcher(data);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + "********");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
