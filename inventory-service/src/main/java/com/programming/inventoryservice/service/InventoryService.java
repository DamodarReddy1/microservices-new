package com.programming.inventoryservice.service;


import com.programming.inventoryservice.dto.InventoryResponse;
import com.programming.inventoryservice.model.Inventory;
import com.programming.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) throws InterruptedException {
        logger.info("Checking Inventory");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(this::mapToInventoryResponse
                ).toList();
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return new InventoryResponse(inventory.getSkuCode(),inventory.getQuantity()>0);
    }
}
