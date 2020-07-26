package com.example.api.repository;

import com.example.api.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, CustomStudentRepository/*, JpaSpecificationExecutor<Student>*/ {
    @Modifying
    @Query("DELETE FROM Student s")
    void deleteAllStudents();
}
