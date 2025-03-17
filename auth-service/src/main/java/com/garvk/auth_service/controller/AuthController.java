package com.garvk.auth_service.controller;

import com.garvk.auth_service.model.UserCred;
import com.garvk.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCred aInUserCred){
        return authService.saveUser(aInUserCred);
    }

    @GetMapping("/getToken")
    public String getToken(UserCred aInUserCred){
        return authService.generateToken(aInUserCred.getName());
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
