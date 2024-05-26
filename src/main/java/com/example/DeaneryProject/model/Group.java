package com.example.DeaneryProject.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "`groups`")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "group_leader_id")
    private User groupLeader;

    private LocalDateTime dateOfCreation;

    private LocalDateTime dateOfModification;

    @ManyToOne(fetch = FetchType.EAGER)
    private User lastModifiedBy;

    @PrePersist
    private void init() {
        dateOfCreation = LocalDateTime.now();
        dateOfModification = dateOfCreation;
    }

    public Date getDateOfModificationAsDate() {
        return Date.from(dateOfModification.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}