package com.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.model.data.UserData;
import com.task.model.dto.RestResponse;
import com.task.model.dto.UserInfoDto;
import com.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = UsersApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String firstName = "Oleksandr";
    private String lastName = "Rudenko";
    private String validEmail = "test@example.com";
    private LocalDate validBirthDate = LocalDate.of(2000, 1, 1);
    private String invalidFirstName = "TestInv";
    private String invalidEmail = "email";
    private LocalDate invalidBirthDate = LocalDate.now().minusYears(17);

    private String validBody = """
              {
                  "firstName": "%s",
                  "lastName": "%s",
                  "birthDate": "%s",
                  "email": "%s"
              }               
            """.formatted(firstName, lastName, validBirthDate, validEmail);


    private String invalidBody = """
              {
                  "firstName": "%s",
                  "lastName": "%s",
                  "birthDate": "%s"
              }
            """.formatted(invalidFirstName, lastName, validBirthDate);

    private String invalidEmailBody = """
              {
                  "firstName": "%s",
                  "lastName": "%s",
                  "birthDate": "%s",
                  "email": "%s"
              }
            """.formatted(firstName, lastName, validBirthDate, invalidEmail);

    private String invalidAgeBody = """
              {
                  "firstName": "%s",
                  "lastName": "%s",
                  "birthDate": "%s",
                  "email": "%s"
              }
            """.formatted(firstName, lastName, invalidBirthDate, validEmail);

    @Test
    public void testCreateUser() throws Exception {
        // Act & Assert
        MvcResult mvcResult = mvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody)
                )
                .andExpect(status().isCreated())
                .andReturn();

        RestResponse response = parseResponse(mvcResult, RestResponse.class);
        int userId = Integer.parseInt(response.getResult());
        assertThat(userId).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testCreateUser_validation() throws Exception {
        //Required fields
        mvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        //Invalid user age (under 18)
        mvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAgeBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        //Invalid email
        mvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailBody))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testUpdateAllUserFields() throws Exception {
        testCreateUser();
        // Act & Assert
        mvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isOk())
                .andReturn();

        UserData user = userRepository.get(1).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getEmail()).isEqualTo(validEmail);
        assertThat(user.getBirthDate()).isEqualTo(validBirthDate);
    }

    @Test
    public void testUpdateAllUserFields_validation() throws Exception {
        //Invalid body
        mvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        //No such user
        mvc.perform(put("/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testUpdateSomeUserFields() throws Exception {
        testCreateUser();
        // Act & Assert
        mvc.perform(patch("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isOk())
                .andReturn();

        UserData user = userRepository.get(1).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(invalidFirstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getEmail()).isEqualTo(validEmail);
        assertThat(user.getBirthDate()).isEqualTo(validBirthDate);
    }

    @Test
    public void testUpdateSomeUserFields_validation() throws Exception {
        testCreateUser();
        //Invalid email
        mvc.perform(patch("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        //Invalid user age (under 18)
        mvc.perform(patch("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAgeBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        //No such user
        mvc.perform(patch("/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testDeleteUser() throws Exception {
        testCreateUser();
        // Act & Assert
        mvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void testDeleteUser_validation() throws Exception {
        testCreateUser();
        //No such user
        mvc.perform(delete("/user/2"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testSearchUsers() throws Exception {
        testCreateUser();
        testCreateUser();
        testCreateUser();
        // Prepare a valid search body
        String validSearchBody = """
                    {
                        "from": "2000-01-01",
                        "to": "2022-12-31"
                    }
                """;

        // Act & Assert
        MvcResult mvcResult = mvc.perform(post("/user/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSearchBody))
                .andExpect(status().isOk())
                .andReturn();
        List<UserInfoDto> response = new ArrayList<>();
        response = parseResponse(mvcResult, response.getClass());
        assertThat(response.size()).isEqualTo(3);
    }

    @Test
    public void testSearchUsers_validation() throws Exception {
        testCreateUser();
        String invalidSearchBody = """
                    {
                        "from": "2023-01-01",
                        "to": "2022-12-31"
                    }
                """;
        String validSearchBody = """
                    {
                        "from": "2005-01-01",
                        "to": "2022-12-31"
                    }
                """;
        // Wrong date ('From' date must be less than 'To' date)
        mvc.perform(post("/user/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidSearchBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        // Valid date, no content
        mvc.perform(post("/user/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSearchBody))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private <T> T parseResponse(MvcResult mvcResult, Class<T> c) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error parsing json", e);
        }
    }
}