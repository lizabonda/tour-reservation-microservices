package cz.cvut.fel.nss.user.controller;

import cz.cvut.fel.nss.entity.Person;
import cz.cvut.fel.nss.user.dto.PersonDto;
import cz.cvut.fel.nss.user.dto.mapper.PersonMapper;
import cz.cvut.fel.nss.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PersonMapper personMapper;

    public UserController(UserService userService, PersonMapper personMapper) {
        this.userService = userService;
        this.personMapper = personMapper;
    }

    @PostMapping("/find-or-create")
    public List<PersonDto> foundOrCreatePersons(@RequestBody List<PersonDto> personsDto) {
        List<Person> persons = userService.foundOrCreatePersons(personsDto);
        return persons.stream()
                .map(personMapper::personToPersonDto)
                .collect(Collectors.toList());
    }
}
