package com.example.api.service;

import com.example.api.model.Student;
import com.example.api.repository.StudentRepository;
import com.example.api.repository.util.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    @Override
    public void deleteAll() {
        studentRepository.deleteAll();
    }

    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public boolean existsById(long id) {
        return studentRepository.existsById(id);
    }

    @Override
    public void deleteById(long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Optional<Student> findById(long id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<Student> search(List<SearchCriteria> criteria) {
        return studentRepository.searchStudents(criteria);
    }

//    @Override
//    public List<Student> findAll(Specification<Student> specification) {
//        return studentRepository.findAll(specification);
//    }


    @Override
    public Page<Student> findPaged(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }
}
