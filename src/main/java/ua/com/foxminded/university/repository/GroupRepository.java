package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.Group;

public interface GroupRepository extends JpaRepository<Group, Integer> {
}
