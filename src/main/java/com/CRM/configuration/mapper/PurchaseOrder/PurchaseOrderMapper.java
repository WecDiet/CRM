package com.CRM.configuration.mapper.PurchaseOrder;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.CRM.model.PurchaseOrder;
import com.CRM.response.PurchaseOrder.PurchaseOrderDetailResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseOrderMapper {
    
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setup() {
        modelMapper.typeMap(PurchaseOrder.class, PurchaseOrderDetailResponse.class)
                    .addMappings(mapping -> {
                        mapping.map(entity -> entity.getName(), PurchaseOrderDetailResponse::setName);
                        mapping.map(entity -> entity.getPoNumber(), PurchaseOrderDetailResponse::setPoNumber);
                        mapping.map(entity -> entity.getOrderDate(), PurchaseOrderDetailResponse::setOrderDate);
                        mapping.map(entity -> entity.getExpectedDeliveryDate(), PurchaseOrderDetailResponse::setExpectedDeliveryDate);
                        mapping.map(entity -> entity.getItems(), PurchaseOrderDetailResponse::setItems);

                    });
    }
}
