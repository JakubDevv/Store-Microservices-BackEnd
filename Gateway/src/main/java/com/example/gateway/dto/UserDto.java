package com.example.gateway.dto;

import java.util.List;

public record UserDto(int id,
                      List<String> roles) {

}
