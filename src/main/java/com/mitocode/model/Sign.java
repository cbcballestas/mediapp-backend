package com.mitocode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idSign;

    private String temperature;
    private String pulse;
    private String rate;
    private LocalDate trackDate;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false, foreignKey = @ForeignKey(name = "FK_SIGN_PATIENT"))
    private Patient patient;
}
