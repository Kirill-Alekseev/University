package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.repository.FacultyRepository;
import ua.com.foxminded.university.repository.SpecialtyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpecialtyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private SpecialtyRepository specialtyRepository;
    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    public void shouldCreateSpecialty() {
        Faculty faculty = new Faculty(1);
        Specialty specialty = new Specialty("specialty");
        specialty.setFaculty(faculty);

        whenRefreshFaculty(specialty);

        specialtyService.create(specialty);

        verify(specialtyRepository).save(specialty);
    }

    @Test
    public void shouldUpdateSpecialty() {
        Faculty faculty = new Faculty(1);
        Specialty specialty = new Specialty("specialty");
        specialty.setId(1);
        specialty.setFaculty(faculty);
        specialty.setVersion(0L);

        when(specialtyRepository.getOne(specialty.getId())).thenReturn(specialty);
        whenRefreshFaculty(specialty);

        specialtyService.update(specialty);

        verify(specialtyRepository).save(specialty);
    }

    @Test
    public void shouldDeleteSpecialty() {
        specialtyService.delete(1);

        verify(specialtyRepository).deleteById(1);
    }

    @Test
    public void shouldGetSpecialty() {
        Specialty specialty = new Specialty("specialty");
        specialty.setId(1);

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));

        assertEquals(specialty, specialtyService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindSpecialtyByNonExistentId() {
        when(specialtyRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> specialtyService.get(0));
    }

    @Test
    public void shouldGetAllSpecialties() {
        List<Specialty> specialties = new ArrayList<>();
        Specialty specialty1 = new Specialty("specialty1");
        specialty1.setId(1);
        Specialty specialty2 = new Specialty("specialty2");
        specialty2.setId(2);
        specialties.add(specialty1);
        specialties.add(specialty2);

        when(specialtyRepository.findAll()).thenReturn(specialties);

        assertEquals(specialties, specialtyService.getAll());
    }

    private void whenRefreshFaculty(Specialty specialty) {
        Faculty faculty = new Faculty();
        faculty.setVersion(0L);

        when(facultyRepository.getOne(specialty.getFaculty().getId())).thenReturn(faculty);
    }
}
