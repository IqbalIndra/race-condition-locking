package com.learn.optlocking.service;

import com.learn.optlocking.model.Item;
import com.learn.optlocking.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementAmount(Long id, Double amount){
        Item item = itemRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        item.setAmount(item.getAmount() + amount);
    }
}
