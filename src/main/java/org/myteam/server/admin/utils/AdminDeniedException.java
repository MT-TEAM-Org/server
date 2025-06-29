package org.myteam.server.admin.utils;

public class AdminDeniedException extends RuntimeException{

    public AdminDeniedException(String message) {
        super(message);
    }
}
