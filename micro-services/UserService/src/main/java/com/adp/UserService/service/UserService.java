package com.adp.UserService.service;

import com.adp.UserService.entities.User;
import com.adp.UserService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> read() {
        return userRepository.findAll();
    }

    public User read(Integer id) {

        return userRepository.findById(id).orElse(null);
    }

    public User update(User user) {
        User temp = read(user.getId());
        if (temp != null) {
            temp = user;
            userRepository.save(temp);
        }
        return temp;
    }


    public User delete(Integer id) {
        User user = read(id);
        {
            if (user != null) {
                userRepository.delete(user);
            }
        }
        return user;
    }

    public Integer getManagerId(Integer userId) {
        return userRepository.findManagerId(userId).orElse(null);
    }

    public List<User> getEmployeeDetails(Integer managerId) {
        List<User>userList = userRepository.findByManagerId(managerId);

        if (userList != null) {
            return userList;
        }

        return null;
    }

    public boolean isUserAccessingItself(Authentication authentication, Integer userId){
        UserDetails userDetails  = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        return authenticatedUserId.equals(userId);
    }

    public boolean isManagerAccessingOwnEmployees(Authentication authentication, Integer userId) {
        UserDetails userDetails  = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        System.out.println(authenticatedUserId);
        Optional<Integer> managerId = userRepository.findManagerId(userId);
        return authenticatedUserId.equals(managerId.get());
    }

    public boolean isMangerAccessingOwnProfile(Authentication authentication, Integer managerId){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        return authenticatedUserId.equals(managerId);

    }

}