package ua.com.foxminded.university.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(of = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Slice<T> {

    private List<T> items;
}
