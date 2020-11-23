package com.juns.pay.user.service;

import com.juns.pay.user.model.User;
import com.juns.pay.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserService {

    @Autowired(required = false)
    UserRepository userRepository;

    public void registerUser(User user) {
        this.userRepository.save(user);
    }

    public User getUser(long userId) {
        return this.userRepository.findById(userId);
    }
}
