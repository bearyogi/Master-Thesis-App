package com.mm.masterthesis.repository;

import com.mm.masterthesis.domain.Credential;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;



public class CredentialRepositoryImpl implements CredRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Credential> findAllByUserpass(String id) {
        TypedQuery<Credential> query = entityManager.createQuery("SELECT t FROM Credential t where t.userpass="+id, Credential.class);
        return query.getResultList();
    }
}
