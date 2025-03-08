package dev.amrish.userservice.services;

import dev.amrish.userservice.exception.PasswordInCorrectException;
import dev.amrish.userservice.exception.TokenExperiedException;
import dev.amrish.userservice.exception.UserFoundException;
import dev.amrish.userservice.models.Token;
import dev.amrish.userservice.models.User;

public interface UserService {
    public Token login(String email, String password) throws UserFoundException, PasswordInCorrectException;
    public User signUp(String name, String email, String password) throws UserFoundException;
    public User validateToken(String token) throws TokenExperiedException;
    public void logout(String token) throws TokenExperiedException;
}
