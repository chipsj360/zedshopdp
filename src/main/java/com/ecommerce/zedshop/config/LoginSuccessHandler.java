package com.ecommerce.zedshop.config;

import com.ecommerce.zedshop.service.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String redirectURL = request.getContextPath();



        if (userDetails.hasRole("ADMIN")) {
            redirectURL = "dashboard";
        } else if (userDetails.hasRole("CUSTOMER")) {
            redirectURL = "/";
        }
        // Get the originally requested URL
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null) {
            String originalURL = savedRequest.getRedirectUrl();
            if (originalURL != null && !originalURL.contains("/login?logout")) {
                redirectURL = originalURL;
            }
        }

        response.sendRedirect(redirectURL);

    }
}
