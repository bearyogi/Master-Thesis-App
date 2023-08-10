package com.mm.masterthesis.repository;

import com.mm.masterthesis.domain.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CredRepository  {
    List<Credential> findAllByUserpass(String id);
}
