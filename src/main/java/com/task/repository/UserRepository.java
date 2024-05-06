package com.task.repository;

import com.task.model.data.UserData;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    private final Map<Integer, UserData> data = new HashMap<>();
    private AtomicInteger idGenerator = new AtomicInteger();

    public int save(UserData userData) {
        if (userData.getId() < 1) {
            userData.setId(idGenerator.incrementAndGet());
        }
        data.put(userData.getId(), userData);
        return userData.getId();
    }

    public Optional<UserData> findById(int id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<UserData> findByBirthDateBetween(LocalDate from, LocalDate to) {
        return data.values().stream()
                .filter(user -> !user.getBirthDate().isBefore(from) && !user.getBirthDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public void deleteById(int id) {
        data.remove(id);
    }

    public Optional<UserData> get(int id) {
        return Optional.ofNullable(data.get(id));
    }
}
