package com.example.ewaste.Entities;

public enum Sender {
    USER("user-message"),
    BOT("bot-message"),
    BOT_LOADING("bot-loading"),
    BOT_ERROR("bot-error");

    public final String styleClass;

    Sender(String styleClass) {
        this.styleClass = styleClass;
    }
}