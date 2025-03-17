package com.garvk.auth_service.service;

import com.garvk.auth_service.model.UserCred;
import com.garvk.auth_service.model.UserDto;
import com.garvk.auth_service.repository.UserCredRepo;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class AbstractUserDetailsService implements UserDetailsService {

    @Autowired
    private UserCredRepo userCredRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCred lUserCred = userCredRepo.findByName(username).orElse(null);
        if(null == lUserCred){
            throw new RuntimeException("No User Found");
        }
        return new UserDto(lUserCred);
    }
}
