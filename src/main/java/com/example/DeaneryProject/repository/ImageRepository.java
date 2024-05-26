package com.example.DeaneryProject.repository;

import com.example.DeaneryProject.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
