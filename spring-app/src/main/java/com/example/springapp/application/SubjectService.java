package com.example.springapp.application;

import com.example.springapp.domain.Major;
import com.example.springapp.domain.Subject;
import com.example.springapp.domain.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final MajorService majorService;
    private final SubjectRepository subjectRepository;

    public void save(String name, String englishName, String majorName) {
        Major major = majorService.mustFind(majorName);
        Subject subject = new Subject(name, englishName, major);
        subjectRepository.insert(subject);
    }

    public void save(String name, String englishName) {
        Subject subject = new Subject(name, englishName, null);
        subjectRepository.insert(subject);
    }

    public Subject mustFind(String subjectName) {
        return subjectRepository.joinFind(subjectName, "major").orElseThrow();
    }

    public List<Subject> findAllByMajorName(String majorName) {
        Predicate<Subject> nonNull = subject -> subject.getMajor() != null;
        Predicate<Subject> equal = subject -> subject.getMajor().getMajorName().equals(majorName);
        Predicate<Subject> where = nonNull.and(equal);

        return subjectRepository.list(where);
    }

    public List<Subject> findAllElectives() {
        Predicate<Subject> where = subject -> subject.getMajor() == null;
        return subjectRepository.list(where);
    }

    public List<Subject> findAll() {
        return subjectRepository.listAll();
    }

}
