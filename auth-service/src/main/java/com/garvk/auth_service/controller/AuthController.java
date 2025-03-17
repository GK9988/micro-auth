package com.garvk.auth_service.controller;

import com.garvk.auth_service.model.AuthDto;
import com.garvk.auth_service.model.UserCred;
import com.garvk.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCred aInUserCred){
        return authService.saveUser(aInUserCred);
    }

    @GetMapping("/login")
    public String getToken(@RequestBody AuthDto aInAuthDto){

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        aInAuthDto.getUsername(),
                        aInAuthDto.getPassword()
                )
        );

        if(auth.isAuthenticated()){
            return authService.generateToken(aInAuthDto.getUsername());
        }

        return "User NOT Present in DB / Invalid";
    }

    @PostMapping("/validate")
    public boolean validateToken(@RequestBody String aInToken){
        try{
            authService.validateTokenFields(aInToken);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
