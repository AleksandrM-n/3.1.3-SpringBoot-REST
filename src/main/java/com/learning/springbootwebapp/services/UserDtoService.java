package com.learning.springbootwebapp.services;

import com.learning.springbootwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.learning.springbootwebapp.model.*;

import java.util.List;

@Component
public class UserDtoService {
    public List<User> users;

    public UserDtoService(List<User> users) {
        this.users = users;
    }


    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
