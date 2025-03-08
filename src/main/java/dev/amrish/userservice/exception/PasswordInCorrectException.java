package dev.amrish.userservice.exception;

public class PasswordInCorrectException extends Exception{

    public PasswordInCorrectException(String message){
        super(message);
    }
}
