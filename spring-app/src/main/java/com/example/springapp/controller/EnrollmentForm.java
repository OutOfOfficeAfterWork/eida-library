package com.example.springapp.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentForm {
    private List<Subject> major;
    private List<Subject> electives;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subject {
        private String subjectName;
        private boolean enrolled;
    }
}
