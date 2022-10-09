package com.example.springapp.controller;

import com.example.springapp.application.MajorService;
import com.example.springapp.application.SubjectService;
import com.example.springapp.domain.Major;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final MajorService majorService;


    @GetMapping("/subject-register")
    public String registerSubject(Model model) {
        model.addAttribute("subjectForm", new SubjectForm());

        List<String> majorFormList = majorService.findAll().stream()
            .map(Major::getMajorName)
            .collect(toList());
        model.addAttribute("majorList", majorFormList);

        return "subject-register";
    }

    @PostMapping("/subject-register")
    public String registerSubject(SubjectForm f) {
        if (!f.getMajorName().isEmpty()) {
            subjectService.save(f.getName(), f.getEnglishName(), f.getMajorName());
        } else {
            subjectService.save(f.getName(), f.getEnglishName());
        }
        return "redirect:/subject-register";
    }

}
