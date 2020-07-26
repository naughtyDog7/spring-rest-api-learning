package com.example.api.model;

import com.example.api.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@Slf4j
@Entity
@Table(name = "students")
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final long id;

    @Column(name = "student_id")
    private final int studentId;

    private final String name;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "study_level")
    private int studyLevel;

    public void validate() {
        if (id == 0 ||
                name == null ||
                groupName == null ||
                studyLevel == 0) {
            log.info("Now here");
            throw new ValidationException("Properties cannot be empty");
        }
    }

}
