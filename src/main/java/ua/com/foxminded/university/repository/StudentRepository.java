package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    int countByGroupId(int groupId);
}
