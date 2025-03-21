package dev.amrish.userservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.amrish.userservice.dtos.SendEmailDto;
import dev.amrish.userservice.exception.PasswordInCorrectException;
import dev.amrish.userservice.exception.TokenExperiedException;
import dev.amrish.userservice.exception.UserFoundException;
import dev.amrish.userservice.models.Token;
import dev.amrish.userservice.models.User;
import dev.amrish.userservice.repositories.TokenRepository;
import dev.amrish.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           TokenRepository tokenRepository,
                           ObjectMapper objectMapper,
                           KafkaTemplate kafkaTemplate) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Token login(String email, String password) throws UserFoundException, PasswordInCorrectException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            throw new UserFoundException("User not Found !!!");
        }

        User user = optionalUser.get();

        if(!bCryptPasswordEncoder.matches(password, user.getHashedPassword())){
            throw new PasswordInCorrectException("Password Incorrect Plz try Again ....");
        }

        Token token = createToken(user);

        return tokenRepository.save(token);
    }



    @Override
    public User signUp(String name, String email, String password) throws UserFoundException {
       Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()) {
            throw  new UserFoundException("User already exists");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        SendEmailDto sendEmailDto = new SendEmailDto();
        sendEmailDto.setFrom("amrish.demo@gmail.com");
        sendEmailDto.setTo(user.getEmail());
        sendEmailDto.setSubject("Welcome");
        sendEmailDto.setBody("Thank you for signing up");

        String sendEmailDtoString = null;
        try {
            sendEmailDtoString = objectMapper.writeValueAsString(sendEmailDto);
        } catch (Exception ex) {
            System.out.println("Something went wrong");
        }

        kafkaTemplate.send("sendEmail", sendEmailDtoString);
        userRepository.save(user);
        return user;
    }

    @Override
    public User validateToken(String token) throws TokenExperiedException {
        System.out.println("Using Validate Token");
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryDateGreaterThan(
                token,
                false,
                new Date()
        );

        if(optionalToken.isEmpty()){
            throw  new TokenExperiedException("Token Expired");
        }

        return optionalToken.get().getUser();
    }

    @Override
    public void logout(String token) throws TokenExperiedException {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeleted(token, false);

        if(optionalToken.isEmpty()){
            throw  new TokenExperiedException("Token Expired");
        }

        Token token1 = optionalToken.get();
        token1.setDeleted(true);
        tokenRepository.save(token1);
    }

    private Token createToken(User user) {
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128)); // Read about UUIDs

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        Date date30DaysFromToday = calendar.getTime();

        token.setExpiryDate(date30DaysFromToday);
        token.setDeleted(false);

        return token;
    }
}
