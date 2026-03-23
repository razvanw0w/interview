package com.interview.resource;

import com.interview.dto.LoginRequest;
import com.interview.dto.LoginResponse;
import com.interview.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginResource {
    private final LoginService loginService;

    @PostMapping
    @ResponseStatus(OK)
    public LoginResponse login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}
