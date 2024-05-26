package com.example.DeaneryProject.controller;

import com.example.DeaneryProject.model.Group;
import com.example.DeaneryProject.model.User;
import com.example.DeaneryProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.DeaneryProject.service.GroupService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    @GetMapping("/")
    public String start(@RequestParam(name = "name", required = false) String name, Model model) {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("loggedInUser", user);
        model.addAttribute("groups", groupService.listGroup(name));
        return "groups";
    }

    @PostMapping("/group/create")
    public String createGroup(@ModelAttribute Group group, RedirectAttributes redirectAttributes) {
        String result = groupService.createGroup(group);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/";
    }

    @PostMapping("/group/{id}/delete")
    public String deleteGroup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String result = groupService.deleteGroup(id);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/";
    }
}
