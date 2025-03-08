package dev.amrish.userservice.repositories;

import dev.amrish.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token save(Token token);

   Optional<Token> findByValueAndDeletedAndExpiryDateGreaterThan (

           String token,
           boolean deleted,
           Date date);

   Optional<Token> findByValueAndDeleted(String token, boolean deleted);



}
