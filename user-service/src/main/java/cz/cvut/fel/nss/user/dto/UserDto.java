package cz.cvut.fel.nss.user.dto;

import cz.cvut.fel.nss.projekt.model.Role;

public record UserDto(Long id, String username, Role role) {}
