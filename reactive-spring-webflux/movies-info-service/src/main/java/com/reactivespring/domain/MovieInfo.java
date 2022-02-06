package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document
public class MovieInfo {

    @Id
    private String movieInfoId;

    @NotBlank(message = "MoieInfo Name must be present !!!")
    private String name;

    @NotNull
    @Positive(message = "MoieInfo year must be positve value!!!")
    private Integer year;

    @NotEmpty(message = "need minium 1 caste")
    private List<@NotBlank(message = "caste cannot be empty") String> cast;
    private LocalDate releaseDate;

}
