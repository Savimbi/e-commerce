package com.example.demo.controller;

import ch.qos.logback.core.net.SMTPAppenderBase;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController(userRepository, cartRepository, encoder);
    }

    @Test
    public void createUserData() {
        when(encoder.encode("12345678")).thenReturn("whoAmI");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("savio");
        request.setPassword("12345678");
        request.setConfirmPassword("12345678");
        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("savio", user.getUsername());
        assertEquals("whoAmI", user.getPassword());
    }

    private User createNewUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("savio");
        user.setPassword("12345678");
        return user;
    }
    private CreateUserRequest dumyUserRequest(){
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("savio");
        request.setPassword("12345678");
        request.setConfirmPassword("12345678");
        return request;
    }


    @Test
    public void validateUserNotFindById() {
        when(encoder.encode(dumyUserRequest().getPassword())).thenReturn("convertedToHashedPassword");
        User savedUser = createNewUser();
        savedUser.setPassword("convertedToHashedPassword");
        when(userRepository.findById(0L)).thenReturn(Optional.of(savedUser));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<User> response = userController.findById(30L,authentication);
        User respondAsUser = response.getBody();

        assertNotEquals(200, response.getStatusCodeValue());
        assertNull(respondAsUser);
    }
    @Test
    public void validateUserFindByUsername() {
        when(userRepository.findByUsername("savio")).thenReturn(createNewUser());
        final ResponseEntity<User> response = userController.findByUserName("savio");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user1 = response.getBody();
        assertNotNull(user1);
        assertEquals("savio", user1.getUsername());
    }

}
