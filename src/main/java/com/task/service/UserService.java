package com.task.service;

import com.task.model.dto.UserDto;
import com.task.model.dto.UserInfoDto;
import com.task.model.dto.UserQueryDto;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface UserService {
    int createUser(UserDto user);
    ResponseEntity<UserInfoDto> updateAllUserFields(int id, UserDto dto);
    ResponseEntity<UserInfoDto> updateSomeUserFields(int id, UserDto dto);
    ResponseEntity<Void> deleteUser(int id);
    ResponseEntity<List<UserInfoDto>> searchUsers(UserQueryDto query);
}
