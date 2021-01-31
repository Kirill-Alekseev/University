package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;
    @InjectMocks
    private FacultyService facultyService;

    @Test
    public void shouldCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("faculty");

        facultyService.create(faculty);

        verify(facultyRepository).save(faculty);
    }

    @Test
    public void shouldUpdateFaculty() {
        Faculty faculty = new Faculty(1);
        faculty.setName("faculty");

        when(facultyRepository.getOne(faculty.getId())).thenReturn(faculty);

        facultyService.update(faculty);

        verify(facultyRepository).save(faculty);
    }

    @Test
    public void shouldDeleteFaculty() {
        facultyService.delete(1);

        verify(facultyRepository).deleteById(1);
    }

    @Test
    public void shouldGetFaculty() {
        Faculty faculty = new Faculty(1);
        faculty.setName("faculty");

        when(facultyRepository.findById(1)).thenReturn(Optional.of(faculty));

        assertEquals(faculty, facultyService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindFacultyByNonExistentId() {
        when(facultyRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> facultyService.get(0));
    }

    @Test
    public void shouldGetAllFaculties() {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty(1));
        faculties.get(0).setName("faculty1");
        faculties.add(new Faculty(2));
        faculties.get(1).setName("faculty2");

        when(facultyRepository.findAll()).thenReturn(faculties);

        assertEquals(faculties, facultyService.getAll());
    }
}
