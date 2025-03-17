package com.garvk.auth_service.service;

import com.garvk.auth_service.model.UserCred;
import com.garvk.auth_service.repository.UserCredRepo;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserCredRepo userCredRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTService jwtService;

    public String saveUser(UserCred aInUserCred){

        aInUserCred.setPassword(passwordEncoder.encode(aInUserCred.getPassword()));

        try{
            userCredRepo.save(aInUserCred);
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }

        return "Successfully Added User";
    }

    public String generateToken(String aInUserName){
        return jwtService.generateToken(aInUserName);
    }
}
