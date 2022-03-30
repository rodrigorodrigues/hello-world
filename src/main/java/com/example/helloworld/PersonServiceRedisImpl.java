package com.example.helloworld;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Profile("redis")
@Service
public class PersonServiceRedisImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public Page<PersonDto> getAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    public Page<PersonDto> getAllByName(String name, Pageable pageable) {
        return personRepository.getAllByName(name, pageable);
    }

    @Override
    public Optional<PersonDto> getById(UUID id) {
        return personRepository.findById(id);
    }

    @Override
    public PersonDto insert(PersonDto personDto) {
        return personRepository.save(personDto);
    }

    @Override
    public PersonDto update(PersonDto personDto) {
        UUID id = personDto.getId();
        if (personRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + id);
        }
        return personRepository.save(personDto);
    }

    @Override
    public void deleteById(UUID id) {
        personRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        personRepository.deleteAll();
    }
}
