package com.cydeo.controller;

import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.UserDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "UserController", description = "User API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RolesAllowed("Admin")
    @Operation(summary = "Get users")
    public ResponseEntity<ResponseWrapper> getUsers() {
        List<UserDTO> userDTOList = userService.listAllUsers();
        return ResponseEntity.ok(new ResponseWrapper("Users are successfully retrieved", userDTOList, HttpStatus.OK));

    }

    @GetMapping("/{username}")
    @RolesAllowed("Admin")
    @Operation(summary = "Get user by user name")
    public ResponseEntity<ResponseWrapper> getUserByUserName(@PathVariable("username") String username) {
        UserDTO user = userService.findByUserName(username);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully retrieved", user, HttpStatus.OK));
    }

    @PostMapping
    @RolesAllowed("Admin")
    @Operation(summary = "Create user")
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO user) {
        userService.save(user);
        return ResponseEntity.ok(new ResponseWrapper("", user, HttpStatus.CREATED));
    }

    @PutMapping
    @RolesAllowed("Admin")
    @Operation(summary = "Update user")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("User is successfully created",HttpStatus.OK));
    }

    @DeleteMapping("/{username}")
    @RolesAllowed("Admin")
    @Operation(summary = "Delete user")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username")String username) throws TicketingProjectException {
        userService.delete(username);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully deleted", HttpStatus.OK));
    }
}
