package com.pharmaops.identity.infrastructure.graphql;

import com.pharmaops.identity.application.port.in.AssignProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final AssignProfileUseCase assignProfileUseCase;

    @MutationMapping
    public boolean assignProfile(@Argument AssignProfileInput input) {
        assignProfileUseCase.assignProfile(new AssignProfileUseCase.Command(
                UUID.fromString(input.userId()),
                UUID.fromString(input.profileId()),
                input.storeId() != null ? UUID.fromString(input.storeId()) : null
        ));
        return true;
    }

    record AssignProfileInput(String userId, String profileId, String storeId) {}
}
