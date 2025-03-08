package dev.amrish.userservice.dtos;

import dev.amrish.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDto {
    private User user; // TODO: Remove user model and use email and password
    private ResponseStatus responseStatus;
}
