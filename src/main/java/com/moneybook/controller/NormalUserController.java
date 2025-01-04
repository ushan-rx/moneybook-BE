package com.moneybook.controller;


import com.moneybook.dto.NormalUserDto;
import com.moneybook.service.NormalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class NormalUserController {

    @Autowired
    private NormalUserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody NormalUserDto normalUserDto) {
        final String message = userService.saveNormalUser(normalUserDto);
        return ResponseEntity.accepted().body(message);

    }

}
