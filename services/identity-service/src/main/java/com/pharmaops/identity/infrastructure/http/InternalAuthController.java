package com.pharmaops.identity.infrastructure.http;

import com.pharmaops.identity.application.port.in.ValidateTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final ValidateTokenUseCase validateTokenUseCase;

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String token = authHeader.substring(7);
            ValidateTokenUseCase.Result result = validateTokenUseCase.validate(token);

            return ResponseEntity.ok()
                    .header("X-User-Id", result.userId().toString())
                    .header("X-User-Roles", String.join(",", result.roles()))
                    .header("X-Store-Id", result.storeId() != null ? result.storeId().toString() : "")
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
