package com.example.helloworld;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HelloWorldApplicationTests {
    @Autowired
    MockMvc client;

    @Autowired
    PersonService personService;

    @BeforeEach
    public void setup() {
        IntStream.rangeClosed(1, 50).forEach(i -> personService.insert(new PersonDto(UUID.randomUUID(), "Test "+i, Instant.now())));
    }

    @Test
    void contextLoads() throws Exception {
        client.perform(get("/api/people"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", hasSize(50)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDto personDto = personService.getAll(Pageable.ofSize(10)).getContent().get(0);

        assertThat(personDto).isNotNull();

        client.perform(get("/api/people/{id}", personDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(personDto.getName()));

        client.perform(post("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(jsonPath("$.id").exists());

        client.perform(delete("/api/people/{id}", personDto.getId()))
                .andExpect(status().isOk());

        client.perform(get("/api/people/{id}", personDto.getId()))
                .andExpect(status().isNotFound());
    }

}
