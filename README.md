# User Service

This service provides a RESTful API to manage users.

## Endpoints

### POST /user
Creates a new user.
- Request body: `UserDto`
- Response: `RestResponse` with the ID of the created user
- Status code: 201 (Created)

### PUT /user/{id}
Updates all fields of a user.
- Path variable: `id` (int) - The ID of the user to update
- Request body: `UserDto`
- Response: `UserInfoDto`
- Status code: 200 (OK)

### PATCH /user/{id}
Updates some fields of a user.
- Path variable: `id` (int) - The ID of the user to update
- Request body: `UserDto`
- Response: `UserInfoDto`
- Status code: 200 (OK)

### DELETE /user/{id}
Deletes a user.
- Path variable: `id` (int) - The ID of the user to delete
- Response: None
- Status code: 200 (OK)

### POST /user/_search
Searches users by birth date range.
- Request body: `UserQueryDto`
- Response: List of `UserInfoDto`
- Status code: 200 (OK) if users are found, 204 (No Content) if no users are found

## Models

### UserDto
- `email`: String, must be a valid email address
- `firstName`: String, must not be blank
- `lastName`: String, must not be blank
- `birthDate`: LocalDate, must be a past date
- `address`: String, optional
- `phoneNumber`: String, optional

### UserQueryDto
- `from`: LocalDate, the start of the birth date range
- `to`: LocalDate, the end of the birth date range
