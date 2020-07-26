package com.example.api.controller;

import com.example.api.controller.resource.event.PagedResourceRetrievedEvent;
import com.example.api.controller.resource.event.SingleResourceRetrievedEvent;
import com.example.api.model.Student;
import com.example.api.repository.util.SearchCriteria;
import com.example.api.service.StudentService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/students")
@Slf4j
@NoArgsConstructor(force = true)
public class StudentController {

    private final StudentService studentService;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public StudentController(StudentService studentService, ApplicationEventPublisher publisher) {
        this.studentService = studentService;
        this.publisher = publisher;
    }

    @GetMapping
    public List<Student> allStudents(@RequestParam(name = "search", required = false) String searchParams) {
        List<Student> result;
        if (searchParams == null) {
            result = studentService.findAll();
        } else if (!searchParams.matches(SearchCriteria.pattern + "+")) {
            log.warn("Couldn't proceed search criteria \"" + searchParams + "\"");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search parameter invalid or unsupported");
        } else {
            result = studentService.search(SearchCriteria.fromString(searchParams));
        }
        return result;
//        return studentService.findAll(SearchCriteria.fromString(searchParams)
//                .stream()
//                .map(StudentSpecification::new)
//                .map(Specification::where)
//                .reduce(Specification::and)
//                .orElse(null));
    }

    @GetMapping(value = "/page")
    public CollectionModel<Student> studentsPaged(@RequestParam int page,
                                                  @RequestParam int size,
                                                  @RequestParam(value = "sortBy", required = false, defaultValue = "") String sortString,
                                                  @RequestParam(required = false, defaultValue = "true") boolean asc) {
        Pageable pageable;
        if (!sortString.isEmpty()) {
            Sort sort = Sort.by(sortString);
            if (!asc)
                sort.descending();
            else
                sort.ascending();
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<Student> students = studentService.findPaged(pageable);
        CollectionModel<Student> result = CollectionModel.of(students);
        publisher.publishEvent(new PagedResourceRetrievedEvent<>(this, result, pageable, students.getTotalPages(), sortString, asc));
        return result;
    }

    //    @CrossOrigin(origins = "http://localhost:8089")
    @GetMapping(value = "/{id}")
    public Student studentById(@PathVariable("id") Long id, HttpServletResponse response) {
        publisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
        return studentService.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Student student) {
        studentService.save(Optional.ofNullable(student)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Student is empty")));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody Student student) {
        studentService.save(
                Optional.ofNullable(student)
                        .filter(i -> studentService.existsById(i.getId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Student is empty or not found for this id")));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") long id) {
        studentService.deleteById(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
    }
}
