package com.example.api.repository.specification;

import com.example.api.model.Student;
import com.example.api.repository.util.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

@RequiredArgsConstructor
public class StudentSpecification implements Specification<Student> {
    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Path<String> key = root.get(criteria.getKey());
        Object value = criteria.getValue();
        Predicate predicate = null;
        switch (criteria.getOperation()) {
            case ">":
                predicate = cb.greaterThan(key, value.toString());
                break;
            case "<":
                predicate = cb.lessThan(key, value.toString());
                break;
            case ":":
                predicate = cb.equal(key, value);
                break;
        }
        return predicate;
    }
}
