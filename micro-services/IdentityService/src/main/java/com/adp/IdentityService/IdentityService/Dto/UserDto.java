package com.adp.IdentityService.IdentityService.Dto;

import com.adp.IdentityService.IdentityService.enums.Department;
import com.adp.IdentityService.IdentityService.enums.JobTitle;
import com.adp.IdentityService.IdentityService.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private JobTitle jobTitle;
    private Department department;
    private String token;
    private Role role;
    private Integer managerId;


}