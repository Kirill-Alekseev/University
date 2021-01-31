package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.repository.CathedraRepository;
import ua.com.foxminded.university.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CathedraServiceTest {

    @Mock
    private CathedraRepository cathedraRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @InjectMocks
    private CathedraService cathedraService;

    @Test
    public void shouldCreateCathedra() {
        Cathedra cathedra = new Cathedra("cathedra");
        cathedra.setFaculty(new Faculty(1));
        cathedra.setId(1);

        whenRefreshFaculty(cathedra);

        cathedraService.create(cathedra);

        verify(cathedraRepository).save(cathedra);
    }

    @Test
    public void shouldUpdateCathedra() {
        Cathedra cathedra = new Cathedra("cathedra");
        cathedra.setFaculty(new Faculty(1));
        cathedra.setId(1);

        when(cathedraRepository.getOne(cathedra.getId())).thenReturn(cathedra);
        whenRefreshFaculty(cathedra);

        cathedraService.update(cathedra);

        verify(cathedraRepository).save(cathedra);
    }

    @Test
    public void shouldDeleteCathedra() {
        cathedraService.delete(1);

        verify(cathedraRepository).deleteById(1);
    }

    @Test
    public void shouldGetCathedra() {
        Cathedra cathedra = new Cathedra("cathedra");
        cathedra.setId(1);

        when(cathedraRepository.findById(1)).thenReturn(Optional.of(cathedra));

        assertEquals(cathedra, cathedraService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindCathedraByNonExistentId() {
        when(cathedraRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> cathedraService.get(0));
    }

    @Test
    public void shouldGetAllCathedras() {
        List<Cathedra> cathedras = new ArrayList<>();
        cathedras.add(new Cathedra("cathedra1"));
        cathedras.get(0).setId(1);
        cathedras.add(new Cathedra("cathedra2"));
        cathedras.get(0).setId(2);

        when(cathedraRepository.findAll()).thenReturn(cathedras);

        assertEquals(cathedras, cathedraService.getAll());
    }

    public void whenRefreshFaculty(Cathedra cathedra) {
        when(facultyRepository.getOne(cathedra.getFaculty().getId())).thenReturn(getFaculty());
    }

    private Faculty getFaculty() {
        Faculty faculty = new Faculty(1);
        faculty.setName("faculty");
        faculty.setVersion(0L);

        return faculty;
    }
}
