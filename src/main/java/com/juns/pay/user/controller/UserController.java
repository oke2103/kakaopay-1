package com.juns.pay.user.controller;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.user.controller.request.SignUpRequest;
import com.juns.pay.user.model.User;
import com.juns.pay.user.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity createUser(@RequestBody @Valid SignUpRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return this.badRequest(errors);
        }
        User user = new User();
        user.setName(request.getName());
        this.userService.registerUser(user);

        final DefaultResponse response = new DefaultResponse(ResultEnum.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);
    }

}
