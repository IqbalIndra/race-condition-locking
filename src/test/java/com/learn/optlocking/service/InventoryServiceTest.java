package com.learn.optlocking.service;

import com.learn.optlocking.model.Item;
import com.learn.optlocking.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class InventoryServiceTest {

    @Autowired private InventoryService inventoryService;
    @Autowired private ItemRepository itemRepository;
    @SpyBean private ItemService itemService;

    private final List<Double> itemAmounts = Arrays.asList(10d , 5d);

    @Test
    void shouldIncrementProductAmount_withoutConcurrency() {
        final Item srcItem = itemRepository.save(new Item());
        assertEquals(0, srcItem.getVersion());

        //when
        for(Double amount :itemAmounts){
            inventoryService.incrementProductAmount(srcItem.getId(), amount);
        }

        final Item item = itemRepository.findById(srcItem.getId()).orElseThrow(() -> new IllegalArgumentException("No item found!"));

        assertAll(
                () -> assertEquals(2, item.getVersion()),
                () -> assertEquals(15d, item.getAmount()),
                () -> verify(itemService, times(2)).incrementAmount(anyLong(), anyDouble())
        );
    }

    @Test
    void shouldIncrementItemAmount_withOptimisticLockingHandling() throws InterruptedException {
        // given
        final Item srcItem = itemRepository.save(new Item());
        assertEquals(0, srcItem.getVersion());

        // when
        final ExecutorService executor = Executors.newFixedThreadPool(itemAmounts.size());

        for (final Double amount : itemAmounts) {
            executor.execute(() -> inventoryService.incrementProductAmount(srcItem.getId(), amount));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

        // then
        final Item item = itemRepository.findById(srcItem.getId()).orElseThrow(() -> new IllegalArgumentException("No item found!"));

        assertAll(
                () -> assertEquals(2, item.getVersion()),
                () -> assertEquals(15, item.getAmount()),
                () -> verify(itemService, times(3)).incrementAmount(anyLong(), anyDouble())
        );
    }
}