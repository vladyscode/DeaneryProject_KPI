package com.example.DeaneryProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String originalFileName;
    private Long size;
    private String contentType;
    @Lob
    private byte[] bytes;
    @OneToOne(mappedBy = "profileImage")
    private User user;
}
