package com.pharmaops.identity.application.usecase;

import com.pharmaops.identity.application.port.in.AssignProfileUseCase;
import com.pharmaops.identity.application.port.out.ProfileRepository;
import com.pharmaops.identity.application.port.out.StoreRepository;
import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.domain.exception.ProfileNotFoundException;
import com.pharmaops.identity.domain.exception.StoreNotFoundException;
import com.pharmaops.identity.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssignProfileUseCaseImpl implements AssignProfileUseCase {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final StoreRepository storeRepository;

    @Override
    public void assignProfile(Command command) {
        userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId().toString()));

        profileRepository.findById(command.profileId())
                .orElseThrow(() -> new ProfileNotFoundException(command.profileId().toString()));

        if (command.storeId() != null) {
            storeRepository.findById(command.storeId())
                    .orElseThrow(() -> new StoreNotFoundException(command.storeId().toString()));
        }

        profileRepository.assignToUser(command.userId(), command.profileId(), command.storeId());
    }
}
