package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Classroom;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
}
