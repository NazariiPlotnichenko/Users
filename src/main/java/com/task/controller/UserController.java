package com.task.controller;

import com.task.model.dto.RestResponse;
import com.task.model.dto.UserDto;
import com.task.model.dto.UserInfoDto;
import com.task.model.dto.UserQueryDto;
import com.task.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createUser(@Valid @RequestBody UserDto user) {
        int id = userService.createUser(user);
        return new RestResponse(String.valueOf(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserInfoDto> updateAllUserFields(@PathVariable int id, @Valid @RequestBody UserDto userDto) {
        return userService.updateAllUserFields(id, userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserInfoDto> updateSomeUserFields(@PathVariable int id, @Valid @RequestBody UserDto userDto) {
        return userService.updateSomeUserFields(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

    @PostMapping("_search")
    public ResponseEntity<List<UserInfoDto>> searchUsers(@RequestBody UserQueryDto query) {
        return userService.searchUsers(query);
    }
}
