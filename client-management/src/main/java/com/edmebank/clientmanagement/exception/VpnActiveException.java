package com.edmebank.clientmanagement.exception;

public class VpnActiveException extends RuntimeException {
    public VpnActiveException() {
        super("Ошибка: Отключите VPN и попробуйте снова.");
    }

    public VpnActiveException(String message) {
        super(message);
    }
}