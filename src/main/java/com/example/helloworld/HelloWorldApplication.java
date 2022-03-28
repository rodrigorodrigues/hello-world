package com.example.helloworld;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }

}

@AllArgsConstructor
@RestController
@RequestMapping("/")
class PersonController {
    private final PersonService personService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Long> index() {
        return ResponseEntity.ok(personService.count());
    }

    @GetMapping("/id")
    ResponseEntity<PersonDto> getById(@PathVariable UUID id) {
        return null;//ResponseEntity.ok(personService.get);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PersonDto> insert(@RequestBody @Validated PersonDto personDto) {
        personService.insert(personDto);
        return ResponseEntity.created(URI.create("/" + personDto.getId()))
                .body(personDto);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PersonDto> update(@RequestBody @Validated PersonDto personDto) {
        personService.update(personDto);
        return ResponseEntity.ok(personDto);
    }

    @DeleteMapping("/id")
    void deleteById(@PathVariable UUID id) {
        personService.deleteById(id);
    }

    @DeleteMapping("/all")
    void deleteAll() {
        personService.deleteAll();
    }
}

interface PersonService {
    long count();
    List<PersonDto> getAll();
    PersonDto insert(PersonDto personDto);
    PersonDto update(PersonDto personDto);
    void deleteById(UUID id);
    void deleteAll();
}

@Profile("!redis")
@Service
class PersonServiceImpl implements PersonService {
    private ConcurrentHashMap<UUID, PersonDto> map = new ConcurrentHashMap<>();

    @Override
    public long count() {
        return map.size();
    }

    @Override
    public List<PersonDto> getAll() {
        return new ArrayList<>(map.values());
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

@AllArgsConstructor
@Profile("redis")
@Service
class PersonServiceRedisImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public long count() {
        return personRepository.count();
    }

    @Override
    public List<PersonDto> getAll() {
        return StreamSupport.stream(personRepository.findAll().spliterator(), false).collect(Collectors.toList());
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


@Profile("redis")
@Repository
interface PersonRepository extends CrudRepository<PersonDto, UUID> {
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Person")
class PersonDto {
    @NotNull
    private UUID id = UUID.randomUUID();
    @NotBlank
    private String name;
}