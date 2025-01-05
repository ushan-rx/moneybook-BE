package com.moneybook.controller;


import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.service.NormalUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base-path}/user")
public class NormalUserController {

    @Autowired
    private NormalUserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody NormalUserCreateDto normalUserCreateDto) {
        final NormalUserDto user = userService.saveNormalUser(normalUserCreateDto);
        return new ResponseEntity<>(user, org.springframework.http.HttpStatus.CREATED);
    }

}
