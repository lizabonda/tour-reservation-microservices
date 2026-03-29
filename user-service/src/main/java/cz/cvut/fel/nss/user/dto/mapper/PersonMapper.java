package cz.cvut.fel.nss.user.dto.mapper;

import cz.cvut.fel.nss.user.dto.PersonDto;
import cz.cvut.fel.nss.projekt.dto.PersonDto;
import cz.cvut.fel.nss.projekt.model.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    Person personDtoToPerson(PersonDto dto);
    PersonDto personToPersonDto(Person person);
}
