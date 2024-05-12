package com.mitocode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExamDTO {

    @EqualsAndHashCode.Include
    private Integer idExam;

    @NotNull
    private String nameExam;

    @NotNull
    private String descriptionExam;
}
