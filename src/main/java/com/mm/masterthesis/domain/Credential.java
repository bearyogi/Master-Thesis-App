package com.mm.masterthesis.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Resource name is mandatory")
    private String resource;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "User id is mandatory")
    private String userpass;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Credential credential = (Credential) o;
        return id != null && Objects.equals(id, credential.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
