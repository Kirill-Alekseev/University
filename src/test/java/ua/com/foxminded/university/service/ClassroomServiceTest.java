package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.repository.ClassroomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClassroomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;
    @InjectMocks
    private ClassroomService classroomService;

    @Test
    public void shouldCreateClassroom() {
        Classroom classroom = new Classroom("111");
        classroom.setCapacity(10);

        classroomService.create(classroom);

        verify(classroomRepository).save(classroom);
    }

    @Test
    public void shouldUpdateClassroom() {
        Classroom classroom = new Classroom(1, "111", 10);

        when(classroomRepository.getOne(classroom.getId())).thenReturn(classroom);

        classroomService.update(classroom);

        verify(classroomRepository).save(classroom);
    }

    @Test
    public void shouldDeleteClassroom() {
        classroomService.delete(1);

        verify(classroomRepository).deleteById(1);
    }

    @Test
    public void shouldGetClassroom() {
        Classroom classroom = new Classroom(1, "111", 10);

        when(classroomRepository.findById(1)).thenReturn(Optional.of(classroom));

        assertEquals(classroom, classroomService.get(1));

    }

    @Test
    public void shouldThrowExceptionWhenFindClassroomByNonExistentId() {
        when(classroomRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> classroomService.get(0));
    }

    @Test
    public void shouldGetAllClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        classrooms.add(new Classroom(1, "111", 10));
        classrooms.add(new Classroom(2, "222", 15));

        when(classroomRepository.findAll()).thenReturn(classrooms);

        assertEquals(classrooms, classroomService.getAll());
    }
}
