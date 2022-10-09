package com.example.springapp.controller;

import lombok.Data;

@Data
public class SignUpForm {
    private String studentCode;
    private String name;
    private String birthDate;
    private String gender;
    private String majorName;
}
