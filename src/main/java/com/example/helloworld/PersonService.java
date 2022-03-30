package com.example.helloworld;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PersonService {
    Page<PersonDto> getAll(Pageable pageable);

    Optional<PersonDto> getById(UUID id);

    Page<PersonDto> getAllByName(String name, Pageable pageable);

    PersonDto insert(PersonDto personDto);

    PersonDto update(PersonDto personDto);

    void deleteById(UUID id);

    void deleteAll();
}
