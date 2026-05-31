package cz.cvut.fel.nss.user.dto;


import cz.cvut.fel.nss.user.Role;

public record UserDto(Long id, String username, Role role) {}
