package com.example.DeaneryProject.controller;

import com.example.DeaneryProject.model.User;
import com.example.DeaneryProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String fatherName,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        String result = userService.updateUserInfo(currentUser, firstName, lastName, fatherName);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());

        if (currentUser != null) {
            String result = userService.changePassword(currentUser, currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("message", result);
        } else {
            redirectAttributes.addFlashAttribute("message", "User not found");
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/set-profile-image")
    public String changeProfileImage(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) throws IOException {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        String result = userService.setProfileImage(currentUser, file);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/profile";
    }

    @GetMapping("/profile/delete")
    public String deleteProfile(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        String result = userService.deleteMyProfile(currentUser);
        redirectAttributes.addFlashAttribute("message", result);
        if (result.equals("Profile deleted successfully")) {
            return "redirect:/login";
        } else {
            return "redirect:/profile";
        }
    }
}