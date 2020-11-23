package com.juns.pay.service.user;

import com.juns.pay.model.user.User;
import com.juns.pay.repository.user.UserRepository;
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
