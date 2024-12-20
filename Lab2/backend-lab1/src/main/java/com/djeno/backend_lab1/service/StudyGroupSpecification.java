package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.models.StudyGroup;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Semester;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class StudyGroupSpecification {

    // Поиск по подстроке в имени группы
    public static Specification<StudyGroup> hasName(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null :
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    // Поиск по подстроке в имени администратора
    public static Specification<StudyGroup> hasAdminName(String adminName) {
        return (root, query, criteriaBuilder) -> adminName == null ? null :
                criteriaBuilder.like(criteriaBuilder.lower(root.get("groupAdmin").get("name")), "%" + adminName.toLowerCase() + "%");
    }

    public static Specification<StudyGroup> hasFormOfEducation(FormOfEducation formOfEducation) {
        return (root, query, criteriaBuilder) -> formOfEducation == null ? null :
                criteriaBuilder.equal(root.get("formOfEducation"), formOfEducation);
    }

    public static Specification<StudyGroup> hasSemester(Semester semester) {
        return (root, query, criteriaBuilder) -> semester == null ? null :
                criteriaBuilder.equal(root.get("semesterEnum"), semester);
    }

    public static Specification<StudyGroup> createdOn(LocalDate creationDate) {
        return (root, query, criteriaBuilder) -> creationDate == null ? null :
                criteriaBuilder.equal(root.get("creationDate"), creationDate);
    }
}
