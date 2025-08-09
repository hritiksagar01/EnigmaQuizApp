package com.sagar.question_service.Dao;

import com.sagar.question_service.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<Student, Long> {

    Optional<Student> findByUniqueIdAndName(String uniqueId, String name);

    Optional<Student> findByUniqueId(String uniqueId);

    @Modifying
    @Query("UPDATE Student s SET s.played = false")
    void resetPlayedStatusForAllStudents();

    // Method to delete student by uniqueId
    void deleteByUniqueId(String uniqueId);
}
