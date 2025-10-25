package com.app.servicea.services;

import com.app.servicea.entity.Users;
import com.app.servicea.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsersServices {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersServices(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users registerNewUser(String email, String password){
       if (usersRepository.findByEmail(email).isPresent()){
           throw new RuntimeException("User with this email already exists");
       }
       Users user = new Users();
       user.setId(UUID.randomUUID());
       user.setEmail(email);
       user.setPasswordHash(passwordEncoder.encode(password));

       return usersRepository.save(user);
    }

    public Optional<Users> findByEmail(String email){
        return usersRepository.findByEmail(email);
    }
}

