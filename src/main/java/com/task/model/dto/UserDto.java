package com.task.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
@Getter
@Setter
@Jacksonized
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Email(message = "Wrong email input")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "FirstName is required")
    private String firstName;

    @NotBlank(message = "LastName is required")
    private String lastName;

    @Past
    @NotNull(message = "BirthDate is required")
    private LocalDate birthDate;

    private String address;
    private String phoneNumber;
}
