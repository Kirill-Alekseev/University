package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@Data
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BaseEntity {

    @Version
    protected Long version;
}
