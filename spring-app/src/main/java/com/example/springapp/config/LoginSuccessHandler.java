package com.example.springapp.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public LoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (session == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String prev = (String) session.getAttribute("prev");
        if (prev == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        session.removeAttribute("prev");
        getRedirectStrategy().sendRedirect(request, response, prev);

    }

}