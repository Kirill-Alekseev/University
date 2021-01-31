package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.LessonTime;

public interface LessonTimeRepository extends JpaRepository<LessonTime, Integer> {
}
