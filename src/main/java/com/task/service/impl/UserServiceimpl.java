package com.task.service.impl;

import com.task.exceptions.NotFoundException;
import com.task.model.data.UserData;
import com.task.model.dto.UserDto;
import com.task.model.dto.UserInfoDto;
import com.task.model.dto.UserQueryDto;
import com.task.repository.UserRepository;
import com.task.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class UserServiceimpl implements UserService {

    private final UserRepository userRepository;
    private static int minAge;

    @Autowired
    public UserServiceimpl(UserRepository userRepository, @Value("${userData.min-age}") int minAge) {
        this.userRepository = userRepository;
        this.minAge = minAge;
    }

    @Override
    public int createUser(UserDto dto) {
        validateStudent(dto);
        UserData data = new UserData();
        updateDataFromDto(data, dto);
        return userRepository.save(data);
    }

    @Override
    public ResponseEntity<UserInfoDto> updateAllUserFields(int id, UserDto dto) {
        UserData data = getOrThrow(id);
        updateDataFromDto(data, dto);
        userRepository.save(data);
        return new ResponseEntity<>(toInfoDto(data), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserInfoDto> updateSomeUserFields(int id, UserDto dto) {
        UserData data = getOrThrow(id);
        if (dto.getEmail() != null) {
            data.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            data.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            data.setLastName(dto.getLastName());
        }
        if (dto.getBirthDate() != null) {
            validateStudent(dto);
            data.setBirthDate(dto.getBirthDate());
        }
        if (dto.getAddress() != null) {
            data.setAddress(dto.getAddress());
        }
        if (dto.getPhoneNumber() != null) {
            data.setPhoneNumber(dto.getPhoneNumber());
        }
        userRepository.save(data);
        return new ResponseEntity<>(toInfoDto(data), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteUser(int id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<UserInfoDto>> searchUsers(UserQueryDto query) {
        if (query.getFrom().isAfter(query.getTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'From' date must be less than 'To' date.");
        }
        List<UserInfoDto> users = userRepository.findByBirthDateBetween(query.getFrom(), query.getTo()).stream()
                .map(this::toInfoDto)
                .toList();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    private void updateDataFromDto(UserData userData, UserDto dto) {
        userData.setEmail(dto.getEmail());
        userData.setFirstName(dto.getFirstName());
        userData.setLastName(dto.getLastName());
        userData.setBirthDate(dto.getBirthDate());
        userData.setAddress(dto.getAddress());
        userData.setPhoneNumber(dto.getPhoneNumber());
    }

    private static void validateStudent(UserDto dto) {
        if (Period.between(dto.getBirthDate(), LocalDate.now()).getYears() < minAge) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is under age.");
        }
    }

    private UserData getOrThrow(int id) {
        return userRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Student with id %d not found".formatted(id)));
    }

    private UserInfoDto toInfoDto(UserData data) {
        return UserInfoDto.builder()
                .id(data.getId())
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .email(data.getEmail())
                .birthDate(data.getBirthDate())
                .phoneNumber(data.getPhoneNumber())
                .address(data.getAddress())
                .build();
    }
}
