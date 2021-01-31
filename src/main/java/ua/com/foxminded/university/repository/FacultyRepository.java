package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
}
