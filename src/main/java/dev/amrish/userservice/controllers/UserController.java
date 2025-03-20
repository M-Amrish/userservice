package dev.amrish.userservice.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.amrish.userservice.dtos.*;
import dev.amrish.userservice.dtos.ResponseStatus;
import dev.amrish.userservice.exception.PasswordInCorrectException;
import dev.amrish.userservice.exception.TokenExperiedException;
import dev.amrish.userservice.exception.UserFoundException;
import dev.amrish.userservice.models.Token;
import dev.amrish.userservice.models.User;
import dev.amrish.userservice.services.UserService;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;



    public UserController(UserService userService) {

        this.userService = userService;

    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto)
            throws UserFoundException, PasswordInCorrectException {

        Token token =   userService.login( loginRequestDto.getEmail(), loginRequestDto.getPassword());
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setToken(token);
        return loginResponseDto;

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto  logoutRequestDto) throws TokenExperiedException {
            userService.logout(logoutRequestDto.getToken());

            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/signup")
    public SignUpResponseDto signup(@RequestBody SignUpRequestDto signUpRequestDto) throws UserFoundException {
        User user = userService.signUp(signUpRequestDto.getName(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword());

        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();
        signUpResponseDto.setUser(user);
        signUpResponseDto.setResponseStatus(ResponseStatus.SUCCESS);



        return signUpResponseDto;
    }


    @PostMapping("/validate")
    public UserDto validateToken(@RequestHeader("Authorization") String token) throws TokenExperiedException {
      User user = userService.validateToken(token);
      return UserDto.fromUser(user);

    }

}
