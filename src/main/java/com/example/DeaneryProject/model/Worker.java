package com.example.DeaneryProject.model;

import com.example.DeaneryProject.model.enums.WorkerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "workers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Worker extends User {
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private WorkerType workerType;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(workerType.name()));
    }
}