package com.mm.masterthesis.service;

import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean auth(User user) {
        return userRepository.findByName(user.getName()) != null;
    }

    public boolean fullAuth(User user) {
        User user1 = userRepository.findByName(user.getName());
        if(user1 != null) {
            return user1.getName().equals(user.getName()) && user1.getPassword().equals(user.getPassword());
        }
        else return false;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
    }
}
