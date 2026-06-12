package com.adp.UserService.repository;


import com.adp.UserService.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "SELECT managerId FROM User_Group3_Oct3_2 WHERE id = :userId", nativeQuery = true)
    Optional<Integer> findManagerId(Integer userId);

    List<User> findByManagerId(Integer managerId);
    
    Optional<User> findByEmail(String email);
}
