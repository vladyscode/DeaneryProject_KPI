package com.example.DeaneryProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "students")
@AllArgsConstructor
@NoArgsConstructor
public class Student extends User {
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;

    @Setter
    @Column(name = "is_leader", nullable = false, columnDefinition = "boolean default false")
    private boolean isLeader = false;

    @Override
    public String toString() {
        return "Student{" +
                "group=" + group +
                '}';
    }
}