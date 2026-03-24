package com.interview.resource;

import com.interview.dto.LoginRequest;
import com.interview.dto.LoginResponse;
import com.interview.openapi.BadRequestApiResponse;
import com.interview.openapi.OkApiResponse;
import com.interview.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations for authentication")
public class LoginResource {

    private final LoginService loginService;

    @PostMapping
    @ResponseStatus(OK)
    @Operation(
            summary = "Login",
            description = "Authenticates a user and returns an access token."
    )
    @OkApiResponse
    @BadRequestApiResponse
    public LoginResponse login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}