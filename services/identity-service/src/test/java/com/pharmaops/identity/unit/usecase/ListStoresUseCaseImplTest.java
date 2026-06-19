package com.pharmaops.identity.unit.usecase;

import com.pharmaops.identity.application.port.out.StoreRepository;
import com.pharmaops.identity.application.usecase.ListStoresUseCaseImpl;
import com.pharmaops.identity.domain.model.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListStoresUseCaseImplTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ListStoresUseCaseImpl useCase;

    @Test
    void listStores_shouldReturnAllStoresFromRepository() {
        // given
        List<Store> stores = List.of(
                buildStore("Farmácia Central"),
                buildStore("Farmácia Norte")
        );
        when(storeRepository.findAll()).thenReturn(stores);

        // when
        List<Store> result = useCase.listStores();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Store::getName)
                .containsExactly("Farmácia Central", "Farmácia Norte");
        verify(storeRepository).findAll();
    }

    private Store buildStore(String name) {
        return Store.builder()
                .id(UUID.randomUUID())
                .name(name)
                .taxId("00.000.000/0001-00")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
