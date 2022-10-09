package com.example.springapp.controller;

import com.example.springapp.application.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;

    @GetMapping("/major-register")
    public String registerMajor(Model model) {
        model.addAttribute("majorRegisterForm", new MajorForm());
        return "major-register";
    }

    @PostMapping("/major-register")
    public String registerMajor(MajorForm majorRegisterForm) {
        String majorName = majorRegisterForm.getMajorName();
        String englishName = majorRegisterForm.getEnglishName();
        // TODO pk unique: 현재는 shard에서 확인 중이나 가비지 데이터 잔존 가능, lib 또는 manager에서 확인 필요.
        majorService.save(majorName, englishName);
        return "redirect:/major-register";
    }
}
