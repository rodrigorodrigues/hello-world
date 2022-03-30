package com.example.helloworld;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Profile("redis")
@Repository
public interface PersonRepository extends PagingAndSortingRepository<PersonDto, UUID> {
    Page<PersonDto> getAllByName(String name, Pageable pageable);
}
