package com.charmroom.charmroom.dto.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscribeDto {
    private Integer id;
    private UserDto subscriber;
    private UserDto target;
}
