package com.task.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserQueryDto {
    private LocalDate from;
    private LocalDate to;
}
