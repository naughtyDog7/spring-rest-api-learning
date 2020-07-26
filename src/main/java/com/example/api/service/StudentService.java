package com.example.api.service;

import com.example.api.model.Student;
import com.example.api.repository.util.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    boolean existsById(long id);
    Student save(Student student);
    void deleteById(long id);
    List<Student> findAll();
    Optional<Student> findById(long id);
//    List<Student> findAll(Specification<Student> specification);

    List<Student> search(List<SearchCriteria> criteria);

    Page<Student> findPaged(Pageable pageable);

    void deleteAll();
}
