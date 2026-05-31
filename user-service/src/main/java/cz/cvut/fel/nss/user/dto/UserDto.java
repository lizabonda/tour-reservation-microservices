package cz.cvut.fel.nss.user.dto;

import cz.cvut.fel.nss.user.entity.Role;

public record UserDto(Long id, String username, Role role) {}
