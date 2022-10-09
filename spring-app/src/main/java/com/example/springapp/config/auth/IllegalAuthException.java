package com.example.springapp.config.auth;

public class IllegalAuthException extends RuntimeException{
    public IllegalAuthException(String criminal) {
        super(criminal +"이 관리자 페이지에 접근을 시도합니다.");
    }
}
