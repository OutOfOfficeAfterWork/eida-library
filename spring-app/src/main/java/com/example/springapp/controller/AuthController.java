package com.example.springapp.controller;

import com.example.springapp.application.MajorService;
import com.example.springapp.application.StudentService;
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
public class AuthController {

    private final StudentService studentService;
    private final MajorService majorService;


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        SignUpForm form = new SignUpForm();
        model.addAttribute("signUpForm", form);

        List<String> majorFormList =  majorService.findAll().stream()
            .map(Major::getMajorName)
            .collect(toList());
        model.addAttribute("majorList", majorFormList);

        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(SignUpForm f) {
        System.out.println(f);
        studentService.save(f.getStudentCode(), f.getName(), f.getBirthDate(), f.getGender(), f.getMajorName());
        return "redirect:" + ("/");
    }
}
