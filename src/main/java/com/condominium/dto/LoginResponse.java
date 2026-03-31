package com.condominium.dto;

public record LoginResponse(
    String token,
    UserDTO user
) {}
