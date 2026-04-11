package com.CRM.configuration.mapper.PurchaseOrder;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.CRM.model.PurchaseOrderItem;
import com.CRM.response.PurchaseOrder.OrderItemResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseOrderItemMapper {
    private final ModelMapper modelMapper;

     @PostConstruct
    public void setup() {
        modelMapper.typeMap(PurchaseOrderItem.class, OrderItemResponse.class)
                .addMappings(mapping -> {
                    mapping.map(entity -> entity.getProductName(), OrderItemResponse::setSnapShotName);
                    mapping.map(entity -> entity.getQuantityOrdered(), OrderItemResponse::setQuantityOrdered);
                    mapping.map(entity -> entity.getQuantityReceived(), OrderItemResponse::setQuantityReceived);
                    mapping.map(entity -> entity.getTaxRate(), OrderItemResponse::setTaxRate);
                    mapping.map(entity -> entity.getUnitPrice(), OrderItemResponse::setUnitPrice);
                    mapping.map(entity -> entity.getProduct().getProductDetail().getName(), OrderItemResponse::setProductName);
                });
    }
}
