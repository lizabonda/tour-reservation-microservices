package cz.cvut.fel.nss.user.dto.mapper;

import cz.cvut.fel.nss.projekt.dto.UserDto;
import cz.cvut.fel.nss.projekt.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userDtoToUser(UserDto dto);
    UserDto userToUserDto(User user);
}
