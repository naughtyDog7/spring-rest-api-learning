package com.example.api.controller;

import com.example.api.SpringRestApiLearningApplication;
import com.example.api.model.Student;
import com.example.api.service.StudentService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringRestApiLearningApplication.class)
@TestInstance(PER_CLASS)
@ActiveProfiles("test")
class EtagTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    private StudentService service;

    @Autowired
    FilterRegistrationBean<ShallowEtagHeaderFilter> filterBean;

    @BeforeAll
    public void setup() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterBean.getFilter()).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void givenResourceExists_whenRetrievingResource_thenEtagHeaderExists() {
        Student student = saveTestStudent(10000, "Muzap", "CIFS555", 1);

        given()
                .when()
                    .get("/students/{id}", student.getId())
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .header(HttpHeaders.ETAG, Matchers.notNullValue());
    }

    @Test
    public void givenResourceRetrieved_whenRetrievingAgainWithEtag_thenNotModifiedReturned() {
        Student student = saveTestStudent(10000, "Bob", "CIFS11", 2);

        String etag = given()
                .accept(APPLICATION_JSON_VALUE)
                    .get("/students/{id}", student.getId())
                        .getHeader(HttpHeaders.ETAG);

        given()
                    .header(HttpHeaders.IF_NONE_MATCH, etag)
                .accept(APPLICATION_JSON_VALUE)
                .when()
                    .get("/students/{id}", student.getId())
                .then()
                    .statusCode(HttpStatus.NOT_MODIFIED.value());
    }

    @Test
    public void whenResourceWasRetrievedThenModified_whenRetrievingAgainWithEtag_thenResourceIsReturned() {
        Student student = saveTestStudent(10001, "Patric", "CIFS00", 6);
        String etag = given()
                .accept(APPLICATION_JSON_VALUE)
                .get("/students/{id}", student.getId())
                    .getHeader(HttpHeaders.ETAG);

        student.setStudyLevel(7);

        given()
                .contentType("application/json; charset=utf-8")
                .body(student)
                .when()
                    .put("/students")
                .then()
                    .statusCode(HttpStatus.OK.value());

        System.out.println("Current students " + service.findAll());
        System.out.println("Student " + student);

        MockMvcResponse response = given()
                .header(HttpHeaders.IF_NONE_MATCH, etag)
                .accept(APPLICATION_JSON_VALUE)
                .get("/students/{id}", student.getId());
        response
                .then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(student, response.getBody().as(Student.class));

    }

    @Test
    public void givenResourceExists_whenRetrievedWithIfMatchEtag_thenResourceIsReceived() {
        Student student = saveTestStudent(10000, "Muzappar", "CIFS32", 1);

        String etag = given()
                .accept(APPLICATION_JSON_VALUE)
                .get("/students/{id}", student.getId())
                .getHeader(HttpHeaders.ETAG);

        student.setStudyLevel(2);
        service.save(student);

        given()
                .header(HttpHeaders.IF_MATCH, etag)
                .accept(APPLICATION_JSON_VALUE)
                .body(student)
                .when()
                    .put("/students")
                .then()
                    .statusCode(HttpStatus.OK.value());
    }

    private Student saveTestStudent(int studentId, String name, String group, int studyLevel) {
        Student student = new Student(0, studentId, name, group, studyLevel);
        return service.save(student);
    }

    @BeforeEach
    public void clearDB() {
        service.deleteAll();
    }
}
