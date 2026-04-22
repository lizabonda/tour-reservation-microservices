package cz.cvut.fel.nss.booking.client;

import cz.cvut.fel.nss.booking.dto.user.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {
    @PostMapping("/users/find-or-create")
    List<PersonDto> findOrCreatePersons(@RequestBody List<PersonDto> personsDto);
}
