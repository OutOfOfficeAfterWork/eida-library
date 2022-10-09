package com.example.springapp.controller;

import com.example.springapp.domain.Major;
import com.example.springapp.domain.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor(access = PRIVATE)
public class SubjectDto {
    private final String name;
    private final String engName;
    private final String majorName;

    public boolean isMajor() {
        return majorName != null;
    }

    public static SubjectDto from(Subject s) {
        String subjectName = s.getSubjectName();
        String englishName = s.getEnglishName();
        String majorName = Optional.ofNullable(s.getMajor()).map(Major::getMajorName).orElse(null);
        return new SubjectDto(subjectName, englishName, majorName);
    }
}
