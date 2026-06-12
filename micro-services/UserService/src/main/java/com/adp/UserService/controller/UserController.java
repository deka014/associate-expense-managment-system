package com.adp.UserService.controller;

import com.adp.UserService.entities.User;
import com.adp.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")

public class UserController {

    @Autowired
    private UserService userService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> retrieveAllUser() {
        List<User> users = userService.read();
        if (users.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(users);
    }


    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('MANAGER') and @userService.isManagerAccessingOwnProfile(authentication,#id)) or (hasAuthority('EMPLOYEE') and @userService.isUserAccessingItself(authentication,#id))")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.read(id);
        if (user != null)
            return ResponseEntity.ok(user);
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')or (hasAuthority('MANAGER') and @userService.isManagerAccessingOwnEmployees(authentication,#updatedUser.id))")
    @PutMapping
    public ResponseEntity<User> updateUserById(@RequestBody User updatedUser) {
        User user = userService.update(updatedUser);
        if (user != null) return ResponseEntity.ok(user);
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('MANAGER') and @userService.isManagerAccessingOwnEmployees(authentication,#id))")
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable Integer id) {
        User user = userService.read(id);
        if (user != null) {
            ResponseEntity.ok(userService.delete(id));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('MANAGER') and @userService.isMangerAccessingOwnProfile(authentication,#managerId))")
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<User>> retrieveEmployeesByManagerId(@PathVariable Integer managerId) {

        List<User> users = userService.getEmployeeDetails(managerId);

        if (users != null) {
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.notFound().build();
    }


}