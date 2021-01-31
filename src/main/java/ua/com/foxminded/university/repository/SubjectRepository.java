package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
