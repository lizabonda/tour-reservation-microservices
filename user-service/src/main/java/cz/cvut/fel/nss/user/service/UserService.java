package cz.cvut.fel.ear.projekt.service;

import cz.cvut.fel.ear.projekt.dao.UserDao;
import cz.cvut.fel.ear.projekt.model.User;
import cz.cvut.fel.nss.user.Person;
import cz.cvut.fel.nss.user.dto.PersonDto;
import cz.cvut.fel.nss.user.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService {
    // We support 2 modes:
    // 1) person.id is set -> use existing Person
    // 2) person.id = null -> create new Person using (firstName, lastName, dateOfBirth)
    private List<Person> foundOrCreatePersons(List<PersonDto> personsDto) {
        final List<Person> persons = new ArrayList<>(personsDto.size());
        for (PersonDto personDto : personsDto) {
            if (personDto == null) {
                throw new IllegalArgumentException("Person must not be null");
            }

            if (personDto.id() != null) {
                final Person person = personDao.find(personDto.id());
                if (person == null) {
                    throw new NotFoundException("Person not found: " + personDto.id());
                }
                persons.add(person);
                continue;
            }

            if (personDto.firstName() == null || personDto.firstName().isBlank()) {
                throw new IllegalArgumentException("Person must have firstName when id is not provided");
            }
            if (personDto.lastName() == null || personDto.lastName().isBlank()) {
                throw new IllegalArgumentException("Person must have lastName when id is not provided");
            }
            if (personDto.dateOfBirth() == null) {
                throw new IllegalArgumentException("Person must have dateOfBirth when id is not provided");
            }

            final Person person = new Person();
            person.setFirstName(personDto.firstName());
            person.setLastName(personDto.lastName());
            person.setDateOfBirth(personDto.dateOfBirth());
            personDao.save(person);
            persons.add(person);
        }
        return persons;
    }

}

