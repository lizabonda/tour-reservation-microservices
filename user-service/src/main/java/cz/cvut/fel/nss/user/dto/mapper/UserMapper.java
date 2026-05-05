package cz.cvut.fel.nss.user.dto.mapper;


import cz.cvut.fel.nss.entity.User;
import cz.cvut.fel.nss.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userDtoToUser(UserDto dto);
    UserDto userToUserDto(User user);
}
