package com.mm.masterthesis.service;

import com.mm.masterthesis.domain.Credential;
import com.mm.masterthesis.repository.CredentialRepository;
import com.mm.masterthesis.repository.CredentialRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;

@Service
@RequiredArgsConstructor
public class CredentialService {
    private final CredentialRepository credentialRepository;
    private final CredentialRepositoryImpl credentialRepositoryImpl;

    public List<Credential> findAllForUser(String id) {
        Connection conn;
        String url = "jdbc:mysql://127.0.0.1:3306/";
        String dbName = "masterthesis";
        String driver = "com.mysql.cj.jdbc.Driver";
        String userName = "yogi";
        String password = "Minotaur21#";

        List<Credential> credentials = new ArrayList<>();
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + dbName, userName, password);
            System.out.println("Connected to the database");

            Statement st = conn.createStatement();
            String query = "SELECT * FROM Credential where userpass=" + id;
            System.out.println(query);
            ResultSet res = st.executeQuery(query);

            while (res.next()) {
                Long userId = res.getLong("id");
                String resource = res.getString("resource");
                String pswd = res.getString("password");
                String userpass = res.getString("userpass");
                credentials.add(new Credential(userId, resource, pswd, userpass));
            }

            conn.close();
    } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {

        }
        return credentials;
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
