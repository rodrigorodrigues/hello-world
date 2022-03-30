package com.example.helloworld;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Profile("!redis")
@Service
public class PersonServiceImpl implements PersonService {
    private ConcurrentHashMap<UUID, PersonDto> map = new ConcurrentHashMap<>();

    @Override
    public Optional<PersonDto> getById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Page<PersonDto> getAll(Pageable pageable) {
        List<PersonDto> list = new ArrayList<>(map.values());
        return PageableExecutionUtils.getPage(list, pageable, list::size);
    }

    @Override
    public Page<PersonDto> getAllByName(String name, Pageable pageable) {
        List<PersonDto> list = map.values().stream().filter(personDto -> personDto.getName().startsWith(name))
                .collect(Collectors.toList());
        return PageableExecutionUtils.getPage(list, pageable, list::size);
    }

    @Override
    public PersonDto insert(PersonDto personDto) {
        return map.putIfAbsent(personDto.getId(), personDto);
    }

    @Override
    public PersonDto update(PersonDto personDto) {
        UUID id = personDto.getId();
        if (!map.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + id);
        }
        map.replace(personDto.getId(), personDto);
        return personDto;
    }

    @Override
    public void deleteById(UUID id) {
        map.remove(id);
    }

    @Override
    public void deleteAll() {
        map.clear();
    }
}
