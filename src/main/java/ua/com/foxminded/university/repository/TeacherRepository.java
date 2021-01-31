package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
