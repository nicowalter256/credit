package com.metropol.credit.models;

import lombok.Data;

@Data
public class Message {
    String message;

    public Message(String message) {
        this.message = message;
    }

}
