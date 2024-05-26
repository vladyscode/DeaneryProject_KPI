package com.example.DeaneryProject.controller;

import com.example.DeaneryProject.model.User;
import com.example.DeaneryProject.model.Worker;
import com.example.DeaneryProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WorkerController {
    private final UserService userService;

    @GetMapping("/workers")
    public String showAllWorkers(Model model, @RequestParam(name = "keyWord", required = false) String keyWord) {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("loggedInUser", user);
        List<User> workers = userService.listWorkers(keyWord);
        model.addAttribute("workers", workers);
        return "workers";
    }

    @GetMapping("/worker/{id}")
    public String workerInfo(@PathVariable Long id, Model model) {
        User loggedInUser = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("loggedInUser", loggedInUser);
        if (loggedInUser.getId().equals(id)) {
            model.addAttribute("user", userService.getUserById(id));
            return "profile";
        }
        Worker worker = (Worker) userService.getUserById(id);
        model.addAttribute("worker", worker);
        return "worker-info";
    }

    @PostMapping("/worker/create")
    public String createWorker(Worker worker, RedirectAttributes redirectAttributes) throws IOException {
        User loggedInUser = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        String result = userService.createWorker(worker, loggedInUser);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/workers";
    }

    @PostMapping("/worker/{workerId}/delete")
    public String deleteWorker(@PathVariable Long workerId, RedirectAttributes redirectAttributes) {
        String result = userService.deleteWorker(workerId);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/workers";
    }
}
