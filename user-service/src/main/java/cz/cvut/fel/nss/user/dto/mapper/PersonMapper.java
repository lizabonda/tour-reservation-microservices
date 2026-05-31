package cz.cvut.fel.nss.user.dto.mapper;

import cz.cvut.fel.nss.user.entity.Person;
import cz.cvut.fel.nss.user.dto.PersonDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    Person personDtoToPerson(PersonDto dto);
    PersonDto personToPersonDto(Person person);
}
