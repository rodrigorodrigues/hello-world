package com.example.helloworld;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/people")
public class PersonController {
    private final PersonService personService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Page<PersonDto>> getAll(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                           @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                           @RequestParam(name = "sort-dir", defaultValue = "desc", required = false) String sortDirection,
                                           @RequestParam(name = "sort-idx", defaultValue = "createdDate", required = false) String[] sortIdx,
                                           @RequestParam(name = "name", required = false) String name) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortIdx));
        return ResponseEntity.ok((StringUtils.hasText(name) ? personService.getAllByName(name, pageRequest) : personService.getAll(pageRequest)));
    }

    @GetMapping("/{id}")
    ResponseEntity<PersonDto> getById(@PathVariable UUID id) {
        return personService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: "+id));

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

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable UUID id) {
        personService.deleteById(id);
    }

    @DeleteMapping("/all")
    void deleteAll() {
        personService.deleteAll();
    }
}
