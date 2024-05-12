package com.mitocode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SignDTO {

    @EqualsAndHashCode.Include
    private Integer idSign;

    @NotNull
    private PatientDTO patient;

    @NotNull
    private String temperature;
    @NotNull
    private String pulse;
    @NotNull
    private String rate;

    @NotNull
    private LocalDate trackDate;
}
