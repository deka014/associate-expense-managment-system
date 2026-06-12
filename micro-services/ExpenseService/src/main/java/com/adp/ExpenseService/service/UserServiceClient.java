package com.adp.ExpenseService.service;


import com.adp.ExpenseService.entities.User;
import com.adp.ExpenseService.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(url = "http://localhost:5002",value = "User-Client",configuration = FeignClientConfig.class)
public interface UserServiceClient {
    @GetMapping("/user/{id}")
    User readUserById(@PathVariable Integer id);

    @GetMapping("user/manager/{managerId}")
    List<User> retrieveEmployeesByManagerId(@PathVariable Integer managerId);


}
