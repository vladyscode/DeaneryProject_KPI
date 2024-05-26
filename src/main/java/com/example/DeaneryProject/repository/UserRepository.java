package com.example.DeaneryProject.repository;

import com.example.DeaneryProject.model.User;
import com.example.DeaneryProject.model.enums.WorkerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE lower(u.firstName) LIKE lower(concat('%', :keyword, '%')) OR lower(u.lastName) LIKE lower(concat('%', :keyword, '%')) OR lower(u.fatherName) LIKE lower(concat('%', :keyword, '%')) OR lower(u.email) LIKE lower(concat('%', :keyword, '%'))")
    List<User> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT u FROM Worker u WHERE u.phoneNumber = :phoneNumber")
    List<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT COUNT(u) FROM User u WHERE u.workerType = :workerType")
    long countByWorkerType(@Param("workerType") WorkerType workerType);
}