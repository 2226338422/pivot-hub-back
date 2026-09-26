package com.pivothub.system.service;

public interface MailCodeService {
    void sendCode(String email, String scene);

    boolean verifyAndConsume(String email, String scene, String code);
}
