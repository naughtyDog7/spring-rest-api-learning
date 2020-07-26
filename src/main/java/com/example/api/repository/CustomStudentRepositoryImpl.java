package com.example.api.repository;

import com.example.api.model.Student;
import com.example.api.repository.util.SearchCriteria;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.function.Consumer;

@Repository
public class CustomStudentRepositoryImpl implements CustomStudentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Student> searchStudents(List<SearchCriteria> params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);

        Predicate predicate = cb.conjunction();

        StudentSearchCriteriaConsumer consumer = new StudentSearchCriteriaConsumer(predicate, cb, root);
        params.forEach(consumer);

        predicate = consumer.predicate;
        cq.where(predicate);


        return entityManager.createQuery(cq).getResultList();
    }

    @AllArgsConstructor
    public static class StudentSearchCriteriaConsumer implements Consumer<SearchCriteria> {

        private Predicate predicate;
        private final CriteriaBuilder cb;
        private final Root<Student> root;

        @Override
        public void accept(SearchCriteria param) {
            Path<String> expression = root.get(param.getKey());
            Object value = param.getValue();
            switch (param.getOperation()) {
                case ">":
                    predicate = cb.and(predicate, cb.greaterThan(expression, value.toString()));
                    break;
                case "<":
                    predicate = cb.and(predicate, cb.lessThan(expression, value.toString()));
                    break;
                case ":":
                    predicate = cb.and(predicate, cb.equal(expression, value));
                    break;
            }
        }
    }
}
