package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {
}
