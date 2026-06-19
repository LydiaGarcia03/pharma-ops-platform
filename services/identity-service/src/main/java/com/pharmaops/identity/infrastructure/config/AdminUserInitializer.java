package com.pharmaops.identity.infrastructure.config;

import com.pharmaops.identity.application.port.in.AssignProfileUseCase;
import com.pharmaops.identity.application.port.in.RegisterUserUseCase;
import com.pharmaops.identity.application.port.out.ProfileRepository;
import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.domain.model.Profile;
import com.pharmaops.identity.domain.model.Role;
import com.pharmaops.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RegisterUserUseCase registerUserUseCase;
    private final AssignProfileUseCase assignProfileUseCase;

    @Value("${ADMIN_EMAIL:admin@pharmaops.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:InitialPassword@2026}")
    private String adminPassword;

    @Value("${ADMIN_NAME:Network Administrator}")
    private String adminName;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initialize() {
        if (userRepository.existsUserWithProfile(Role.ADMIN.name())) {
            log.debug("Admin user already exists, skipping initialization");
            return;
        }

        log.info("No admin user found. Creating initial admin user: {}", adminEmail);

        Profile adminProfile = profileRepository.findByName(Role.ADMIN.name())
                .orElseGet(() -> profileRepository.save(Profile.builder()
                        .id(UUID.randomUUID())
                        .name(Role.ADMIN.name())
                        .description("Network administrator with global access")
                        .build()));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseGet(() -> registerUserUseCase.registerUser(
                        new RegisterUserUseCase.Command(adminName, adminEmail, adminPassword)
                ));

        // Admin must reset password on first login
        userRepository.save(admin.withPasswordResetRequired(true));

        // storeId null = global access (no store restriction)
        assignProfileUseCase.assignProfile(
                new AssignProfileUseCase.Command(admin.getId(), adminProfile.getId(), null)
        );

        log.info("Admin user initialized successfully: {}", adminEmail);
    }
}
