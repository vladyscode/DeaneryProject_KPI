package com.example.DeaneryProject.controller;

import com.example.DeaneryProject.model.Student;
import com.example.DeaneryProject.model.Group;
import com.example.DeaneryProject.service.GroupService;
import com.example.DeaneryProject.service.UserService;
import lombok.RequiredArgsConstructor;
import com.example.DeaneryProject.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentController {
    private final UserService userService;
    private final GroupService groupService;

    @GetMapping("/group/{groupId}/student/{id}")
    public String studentInfo(@PathVariable Long groupId, @PathVariable Long id, Model model) {
        User loggedInUser = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("loggedInUser", loggedInUser);
        if (loggedInUser.getId().equals(id)) {
            model.addAttribute("user", userService.getUserById(id));
            return "profile";
        }
        model.addAttribute("student", userService.getUserById(id));
        return "student-info";
    }

    @GetMapping("/students")
    public String showAllStudents(Model model, @RequestParam(name = "keyWord", required = false) String keyWord) {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("loggedInUser", user);
        List<User> students = userService.listStudents(keyWord);
        model.addAttribute("students", students);
        return "students";
    }

    @GetMapping("/group/{id}")
    public String groupInfo(@PathVariable Long id, @RequestParam(name = "keyWord", required = false) String keyWord, Model model) {
        Group group = groupService.getGroupById(id);
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("loggedInUser", user);
        model.addAttribute("students", userService.listStudentsInGroup(keyWord, group));
        model.addAttribute("group", group);
        return "group-info";
    }

    @PostMapping("/group/{groupId}/student/create")
    public String createStudent(@PathVariable Long groupId, Student student, RedirectAttributes redirectAttributes) throws IOException {
        Group group = groupService.getGroupById(groupId);
        User loggedInUser = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (group != null) {
            String result = userService.createStudent(student, group, loggedInUser);
            redirectAttributes.addFlashAttribute("message", result);
            groupService.updateGroup(group);
        }
        return "redirect:/group/" + groupId;
    }

    @PostMapping("/group/{groupId}/student/{studentId}/delete")
    public String deleteStudent(@PathVariable Long groupId, @PathVariable Long studentId, RedirectAttributes redirectAttributes) {
        String result = userService.deleteStudent(studentId);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/group/" + groupId;
    }

    @PostMapping("/group/{groupId}/student/{studentId}/setLeader")
    public String setStudentAsLeader(@PathVariable Long groupId, @PathVariable Long studentId, RedirectAttributes redirectAttributes) {
        String result = userService.setStudentAsLeader(studentId, groupId);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/group/" + groupId;
    }
}