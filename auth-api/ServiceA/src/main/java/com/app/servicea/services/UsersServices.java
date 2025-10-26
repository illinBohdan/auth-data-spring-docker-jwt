package com.app.servicea.services;

import com.app.servicea.entity.Users;
import com.app.servicea.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    /**
     * Створює нового користувача та зберігає його в базі даних.
     * @param email - пошта користувача.
     * @param password - сирий пароль.
     * @return Збережений об'єкт Users.
     * @throws RuntimeException якщо користувач з такою поштою вже існує.
     */
    @Transactional
    public Users registerNewUser(String email, String password) {

        // Крок 1: Перевірка на вже існуючого користувача (як підтвердили логи Hibernate)
        if (usersRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with this email already exists.");
        }

        // Крок 2: Створення НОВОГО об'єкта
        Users newUser = new Users();

        // Використовуємо UUID як ID, як це визначено в Entity
       // newUser.setId(UUID.randomUUID());
        newUser.setEmail(email);

        // Хешуємо пароль перед збереженням
        String passwordHash = passwordEncoder.encode(password);
        newUser.setPasswordHash(passwordHash);

        // Крок 3: Зберігаємо НОВИЙ об'єкт
        // Тут помилка OptimisticLocking зазвичай НЕ відбувається, якщо це дійсно новий об'єкт.
        // Якщо помилка продовжиться, це означатиме, що сутність Users має неправильно налаштоване поле @Version.
        return usersRepository.save(newUser);
    }
}

