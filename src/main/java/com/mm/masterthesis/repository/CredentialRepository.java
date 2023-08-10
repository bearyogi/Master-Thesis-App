package com.mm.masterthesis.repository;

import com.mm.masterthesis.domain.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CredentialRepository extends JpaRepository<Credential, Long>, CredRepository {
    //List<Credential> findAllByUserpass(String id);
}
