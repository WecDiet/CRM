package com.CRM.configuration.mapper.Inventory;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.CRM.model.Inventory;
import com.CRM.model.InventoryTransaction;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.Inventory.InventoryTransactionResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setup() {
        modelMapper.typeMap(Inventory.class, InventoryResponse.class)
                    .addMappings(mapping -> {
                        mapping.map(entity -> entity.getId(), InventoryResponse::setId);
                        mapping.map(entity -> entity.getProduct().getProductDetail().getId(), InventoryResponse::setProductId);
                        mapping.map(entity -> entity.getProduct().getProductDetail().getName(), InventoryResponse::setProductName);
                        mapping.map(entity -> entity.getProduct().getMainImage().getImageUrl(), InventoryResponse::setMainImage);
                        mapping.map(entity -> entity.getWarehouse().getName(), InventoryResponse::setLocationName);
                        mapping.map(entity -> entity.getWarehouse().getWarehouseType(), InventoryResponse::setLocationType);
                        mapping.map(entity -> entity.getQuantityOnHand(), InventoryResponse::setQuantityOnHand);
                    });

        modelMapper.typeMap(InventoryTransaction.class, InventoryTransactionResponse.class)
                    .addMappings(mapping ->{
                        mapping.map(entity -> entity.getId(), InventoryTransactionResponse::setId);
                        mapping.map(entity -> entity.getProduct().getId(), InventoryTransactionResponse::setProductId);
                        mapping.map(entity -> entity.getProduct().getProductDetail().getName(), InventoryTransactionResponse::setProductName);
                        mapping.map(entity -> entity.getType(), InventoryTransactionResponse::setType);
                        mapping.map(entity -> entity.getQuantity(), InventoryTransactionResponse::setQuantity);
                        mapping.map(entity -> entity.getWarehouse().getName(), InventoryTransactionResponse::setLocationName);
                        mapping.map(entity -> entity.getWarehouse().getWarehouseType(), InventoryTransactionResponse::setLocationType);
                        mapping.map(entity -> entity.getReferenceId(), InventoryTransactionResponse::setReferenceId);
                        mapping.map(entity -> entity.getReferenceCode(), InventoryTransactionResponse::setReferenceCode);
                    }); 
    }
}
