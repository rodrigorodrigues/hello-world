package com.example.helloworld;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Person")
public class PersonDto {
    @NotNull
    private UUID id = UUID.randomUUID();
    @NotBlank
    private String name;
    private Instant createdDate;
}
