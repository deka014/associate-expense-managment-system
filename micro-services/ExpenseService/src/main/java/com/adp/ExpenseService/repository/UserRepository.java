package com.adp.ExpenseService.repository;

import com.adp.ExpenseService.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
  Optional<User> findByEmail(String email);

  @Query(value = "SELECT managerId FROM User_Group3_Oct3_2 WHERE id = :userId", nativeQuery = true)
  Optional<Integer> findManagerId(Integer userId);
//
//    User readUserById(Integer id);
//
//    List<User> getUserByManagerId(Integer id);
  List<User> findByManagerId(Integer managerId);
  
  User readUserById(Integer id);
  List<User> findUsersByManagerId(Integer managerId);
}
