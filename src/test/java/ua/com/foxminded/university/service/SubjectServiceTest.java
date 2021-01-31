package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.repository.CathedraRepository;
import ua.com.foxminded.university.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubjectServiceTest {

    @Mock
    private CathedraRepository cathedraRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @InjectMocks
    private SubjectService subjectService;

    @Test
    public void shouldCreateSubject() {
        Cathedra cathedra = new Cathedra();
        cathedra.setId(1);
        Subject subject = new Subject("subject");
        subject.setCathedra(cathedra);

        whenRefreshCathedra(subject);

        subjectService.create(subject);

        verify(subjectRepository).save(subject);
    }

    @Test
    public void shouldUpdateSubject() {
        Cathedra cathedra = new Cathedra();
        cathedra.setId(1);
        Subject subject = new Subject("subject");
        subject.setId(1);
        subject.setVersion(0L);
        subject.setCathedra(cathedra);

        when(subjectRepository.getOne(subject.getId())).thenReturn(subject);
        whenRefreshCathedra(subject);

        subjectService.update(subject);

        verify(subjectRepository).save(subject);
    }

    @Test
    public void shouldDeleteSubject() {
        subjectService.delete(1);

        verify(subjectRepository).deleteById(1);
    }

    @Test
    public void shouldGetSubject() {
        Subject subject = new Subject("subject");
        subject.setId(1);

        when(subjectRepository.findById(1)).thenReturn(Optional.of(subject));

        assertEquals(subject, subjectService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindSubjectByNonExistentId() {
        when(subjectRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> subjectService.get(0));
    }

    @Test
    public void shouldGetAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        Subject subject1 = new Subject("subject1");
        subject1.setId(1);
        Subject subject2 = new Subject("subject2");
        subject2.setId(2);

        when(subjectRepository.findAll()).thenReturn(subjects);

        assertEquals(subjects, subjectService.getAll());
    }

    private void whenRefreshCathedra(Subject subject) {
        Cathedra cathedra = new Cathedra();
        cathedra.setVersion(0L);

        when(cathedraRepository.getOne(subject.getCathedra().getId())).thenReturn(cathedra);
    }
}
