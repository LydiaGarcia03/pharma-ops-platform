package com.pharmaops.identity.unit.usecase;

import com.pharmaops.identity.application.port.in.CreateStoreUseCase;
import com.pharmaops.identity.application.port.out.StoreRepository;
import com.pharmaops.identity.application.usecase.CreateStoreUseCaseImpl;
import com.pharmaops.identity.domain.model.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateStoreUseCaseImplTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CreateStoreUseCaseImpl useCase;

    @Test
    void createStore_shouldBuildStoreWithCorrectFieldsAndSave() {
        // given
        var command = new CreateStoreUseCase.Command("Farmácia Central", "12.345.678/0001-99");
        var savedStore = Store.builder()
                .id(UUID.randomUUID())
                .name("Farmácia Central")
                .taxId("12.345.678/0001-99")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(storeRepository.save(any(Store.class))).thenReturn(savedStore);

        // when
        Store result = useCase.createStore(command);

        // then
        assertThat(result.getName()).isEqualTo("Farmácia Central");
        assertThat(result.getTaxId()).isEqualTo("12.345.678/0001-99");
        assertThat(result.isActive()).isTrue();
        verify(storeRepository).save(any(Store.class));
    }
}
