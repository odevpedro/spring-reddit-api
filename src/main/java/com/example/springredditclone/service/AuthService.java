package com.example.springredditclone.service;

import com.example.springredditclone.controller.request.RegisterRequest;
import com.example.springredditclone.model.NotificationEmail;
import com.example.springredditclone.model.User;
import com.example.springredditclone.model.VerificationToken;
import com.example.springredditclone.repository.UserRepository;
import com.example.springredditclone.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

//Lógica principal de cadastrar novos usuários!?
@Service
@AllArgsConstructor
public class AuthService {

    //Injeção de dependencias
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

@Transactional
    public void signup(RegisterRequest registerRequest) {
    User user = new User();
    user.setUsername(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setCreated(Instant.now());
    user.setEnabled(false);

    userRepository.save(user);

    String token = generateVerificationToken(user);
    mailService.sendMail(new NotificationEmail("Please Active your Account",
            user.getEmail(), "Thank you" +
            " for signing up to Spring Reddit, " +
            "please click on the below url to activate your account : " +
            "http://localhost:8080/api/auth/accountVerification/" + token));
}

        private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken  verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;

    }

    }

