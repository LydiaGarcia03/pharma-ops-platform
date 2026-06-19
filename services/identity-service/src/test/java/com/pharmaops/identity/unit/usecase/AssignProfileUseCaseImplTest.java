package com.pharmaops.identity.unit.usecase;

import com.pharmaops.identity.application.port.in.AssignProfileUseCase;
import com.pharmaops.identity.application.port.out.ProfileRepository;
import com.pharmaops.identity.application.port.out.StoreRepository;
import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.application.usecase.AssignProfileUseCaseImpl;
import com.pharmaops.identity.domain.exception.ProfileNotFoundException;
import com.pharmaops.identity.domain.exception.StoreNotFoundException;
import com.pharmaops.identity.domain.exception.UserNotFoundException;
import com.pharmaops.identity.domain.model.Profile;
import com.pharmaops.identity.domain.model.Store;
import com.pharmaops.identity.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignProfileUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private AssignProfileUseCaseImpl useCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID profileId = UUID.randomUUID();
    private final UUID storeId = UUID.randomUUID();

    @Test
    void assignProfile_whenAllEntitiesExist_shouldCallAssignToUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser()));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(buildProfile()));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(buildStore()));

        useCase.assignProfile(new AssignProfileUseCase.Command(userId, profileId, storeId));

        verify(profileRepository).assignToUser(userId, profileId, storeId);
    }

    @Test
    void assignProfile_whenStoreIdIsNull_shouldSkipStoreValidationAndAssignGlobally() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser()));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(buildProfile()));

        useCase.assignProfile(new AssignProfileUseCase.Command(userId, profileId, null));

        verify(storeRepository, never()).findById(any());
        verify(profileRepository).assignToUser(userId, profileId, null);
    }

    @Test
    void assignProfile_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> useCase.assignProfile(new AssignProfileUseCase.Command(userId, profileId, storeId)));

        verify(profileRepository, never()).findById(any());
    }

    @Test
    void assignProfile_whenProfileNotFound_shouldThrowProfileNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser()));
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class,
                () -> useCase.assignProfile(new AssignProfileUseCase.Command(userId, profileId, storeId)));

        verify(storeRepository, never()).findById(any());
    }

    @Test
    void assignProfile_whenStoreNotFound_shouldThrowStoreNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser()));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(buildProfile()));
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThrows(StoreNotFoundException.class,
                () -> useCase.assignProfile(new AssignProfileUseCase.Command(userId, profileId, storeId)));

        verify(profileRepository, never()).assignToUser(any(), any(), any());
    }

    private User buildUser() {
        return User.builder().id(userId).name("Ana").email("ana@x.com")
                .passwordHash("hash").active(true).passwordResetRequired(false)
                .createdAt(LocalDateTime.now()).build();
    }

    private Profile buildProfile() {
        return Profile.builder().id(profileId).name("MANAGER").build();
    }

    private Store buildStore() {
        return Store.builder().id(storeId).name("Farmácia Central")
                .taxId("00.000.000/0001-00").active(true).createdAt(LocalDateTime.now()).build();
    }
}
