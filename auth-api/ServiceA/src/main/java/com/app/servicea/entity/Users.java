package com.app.servicea.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
public class Users implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;
    private String email;
    private String passwordHash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // У цьому завданні ролі не потрібні, повертаємо порожню колекцію
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // Повертаємо хешований пароль
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        // Використовуємо email як ім'я користувача
        return this.email;
    }

    // Всі ці методи мають повертати true для активного користувача
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
