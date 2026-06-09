package cz.cvut.fel.nss.user.service;

import cz.cvut.fel.nss.user.entity.Person;
import cz.cvut.fel.nss.user.dao.PersonDao;
import cz.cvut.fel.nss.user.dto.PersonDto;
import cz.cvut.fel.nss.user.dto.mapper.PersonMapper;
import cz.cvut.fel.nss.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final PersonDao personDao;
    private final PersonMapper personMapper;

    public UserService(PersonDao personDao, PersonMapper personMapper) {
        this.personDao = personDao;
        this.personMapper = personMapper;
    }

    // We support 2 modes:
    // 1) person.id is set -> use existing Person
    // 2) person.id = null -> create new Person using (firstName, lastName, dateOfBirth)
    public List<Person> foundOrCreatePersons(List<PersonDto> personsDto) {
        final List<Person> persons = new ArrayList<>(personsDto.size());
        if (personsDto == null || personsDto.isEmpty()) {
            throw new IllegalArgumentException("Persons must not be empty");
        }
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
            if (personDto.dateOfBirth().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Date of birth cannot be in the future");
            }

            Person person = personDao.findByDetails(personDto.firstName(), personDto.lastName(), personDto.dateOfBirth());
            if (person != null) {
                persons.add(person);
                continue;
            }

            person = personMapper.personDtoToPerson(personDto);
            personDao.save(person);
            persons.add(person);
        }
        return persons;
    }

}

