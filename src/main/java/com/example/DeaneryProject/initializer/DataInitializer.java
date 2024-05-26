package com.example.DeaneryProject.initializer;

import com.example.DeaneryProject.model.Worker;
import com.example.DeaneryProject.model.enums.WorkerType;
import com.example.DeaneryProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;

@Component
public class DataInitializer {

    @Autowired
    private UserService userService;

    @PostConstruct
    public void initData() throws IOException {
        Worker systemWorker = new Worker();
        systemWorker.setEmail("mainworker@gmail.com");
        systemWorker.setPassword("Password1@");
        systemWorker.setFirstName("The");
        systemWorker.setLastName("Main");
        systemWorker.setFatherName("Worker");
        systemWorker.setPhoneNumber("+380123456789");
        systemWorker.setWorkerType(WorkerType.ADMINISTRATOR_WORKER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(systemWorker, null, Collections.singletonList(new SimpleGrantedAuthority("ADMINISTRATOR_WORKER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userService.createWorker(systemWorker, null);
    }
}