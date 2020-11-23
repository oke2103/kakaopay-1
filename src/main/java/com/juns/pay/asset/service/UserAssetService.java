package com.juns.pay.asset.service;

import com.juns.pay.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserAssetService {

    public void send(User fromUser, User toUser, double amount) {
        //
    }

    public void send(User fromUser, double amount) {
        // 뿌리기 시, 뿌린 금액 만큼 잔액 감소 로직
    }

    public void receive(User toUser, double amount) {
        // 받기 시, 금액 만큼 잔액 증가 로직
    }
}
