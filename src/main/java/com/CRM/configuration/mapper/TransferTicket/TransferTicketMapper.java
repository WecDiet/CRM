package com.CRM.configuration.mapper.TransferTicket;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.CRM.model.TransferTicket;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.TransferTicket.BaseTransferTicketResponse;
import com.CRM.response.TransferTicket.TransferTicketDetailResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferTicketMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setup() {
        modelMapper.typeMap(TransferTicket.class, BaseTransferTicketResponse.class)
                    .addMappings(mapping -> {
                        mapping.map(entity -> entity.getId(), BaseTransferTicketResponse::setId);
                        mapping.map(entity -> entity.getTicketCode(), BaseTransferTicketResponse::setTicketCode);
                        mapping.map(entity -> entity.getExpectedDeliveryDate(), BaseTransferTicketResponse::setExpectedDeliveryDate);
                        mapping.map(entity -> entity.getStore().getName(), BaseTransferTicketResponse::setStoreName);
                        mapping.map(entity -> entity.getStatus(), BaseTransferTicketResponse::setStatus);
                    });
        
        modelMapper.typeMap(TransferTicket.class, TransferTicketDetailResponse.class)
                    .addMappings(mapping -> {
                        mapping.map(entity -> entity.getId(), TransferTicketDetailResponse::setId);
                        mapping.map(entity -> entity.getTicketCode(), TransferTicketDetailResponse::setTicketCode);
                        mapping.map(entity -> entity.getWarehouse().getName(), TransferTicketDetailResponse::setWarehouseName);
                        mapping.map(entity -> entity.getStore().getName(), TransferTicketDetailResponse::setStoreName);
                        mapping.map(entity -> entity.getExpectedDeliveryDate(), TransferTicketDetailResponse::setExpectedDeliveryDate);
                        mapping.map(entity -> entity.getStatus(), TransferTicketDetailResponse::setStatus);
                        mapping.map(entity -> entity.getNote(), TransferTicketDetailResponse::setNote);
                        mapping.map(entity -> entity.getItems(), TransferTicketDetailResponse::setItems);
                    });            
    }
}
