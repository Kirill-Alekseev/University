package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Cathedra;

public interface CathedraRepository extends JpaRepository<Cathedra, Integer> {
}
