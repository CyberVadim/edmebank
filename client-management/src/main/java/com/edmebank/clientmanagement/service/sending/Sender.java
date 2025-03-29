package com.edmebank.clientmanagement.service.sending;

import com.edmebank.clientmanagement.model.Notification;

public interface Sender {

    void send(Notification notification);
}
