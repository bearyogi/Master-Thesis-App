package com.mm.masterthesis.service;

import com.mm.masterthesis.domain.Credential;
import com.mm.masterthesis.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;

@Service
@RequiredArgsConstructor
public class CredentialService {
    private final CredentialRepository credentialRepository;

    public List<Credential> findAllForUser(String id) {
        return credentialRepository.findAllByUserpass(id);
    }

    public void save(Credential credential) {
        credentialRepository.save(credential);
    }

    public List<Credential> findAll() {
        return credentialRepository.findAll();
    }

    public Optional<Credential> findById(Long id) {
        return credentialRepository.findById(id);
    }

    public void delete(Long id) {
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credential Id:" + id));
        credentialRepository.delete(credential);
    }
}
