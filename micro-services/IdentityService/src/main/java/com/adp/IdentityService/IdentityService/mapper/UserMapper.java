package com.adp.IdentityService.IdentityService.mapper;

import com.adp.IdentityService.IdentityService.entities.User;
import com.adp.IdentityService.IdentityService.Dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

}