package com.example.api.repository;

import com.example.api.model.Student;
import com.example.api.repository.util.SearchCriteria;

import java.util.List;

public interface CustomStudentRepository {
    List<Student> searchStudents(List<SearchCriteria> params);
}
