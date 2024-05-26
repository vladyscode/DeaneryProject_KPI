package com.example.DeaneryProject.repository;

import com.example.DeaneryProject.model.Group;
import com.example.DeaneryProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByName(String name);

    @Query("SELECT g FROM Group g WHERE lower(g.name) LIKE lower(concat('%', :keyWord, '%'))")
    List<Group> findByKeyWord(@Param("keyWord") String keyWord);

    List<Group> findAllByLastModifiedBy(User user);
}