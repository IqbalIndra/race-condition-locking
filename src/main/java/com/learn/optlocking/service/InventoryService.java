package com.learn.optlocking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryService {
    private final ItemService itemService;

    @Transactional(readOnly = true)
    public void incrementProductAmount(Long id, Double amount){
        try {
            itemService.incrementAmount(id,amount);
        }catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Somebody has already updated the amount for item:{} in concurrent transaction. Will try again...", id);
            itemService.incrementAmount(id,amount);
        }
    }
}
