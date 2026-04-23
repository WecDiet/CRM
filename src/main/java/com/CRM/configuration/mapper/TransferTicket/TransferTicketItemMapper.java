package com.CRM.configuration.mapper.TransferTicket;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.CRM.model.PatternColor;
import com.CRM.model.TransferTicketItem;
import com.CRM.response.PatternColor.PatternColorItem;
import com.CRM.response.TransferTicket.TransferItem;
import com.CRM.response.TransferTicket.ConfirmReceipt.ReceiptItemResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferTicketItemMapper {
    
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setup() {
        modelMapper.typeMap(TransferTicketItem.class, TransferItem.class)
            .addMappings(mapping -> {
                mapping.map(entity -> entity.getId(), TransferItem::setId);
                // mapping.map(entity -> entity.getQuantitySent(), TransferItem::setQuantitySent);
                // mapping.map(entity -> entity.getQuantityReceived(), TransferItem::setQuantityReceived);
                mapping.map(entity -> entity.getProduct().getProductDetail().getName(), TransferItem::setProductName);
                mapping.map(entity -> entity.getProduct().getProductDetail().getColors(), TransferItem::setColors);
            });

        modelMapper.typeMap(TransferTicketItem.class, ReceiptItemResponse.class)
            .addMappings(mapping -> {
                mapping.map(entity -> entity.getProduct().getSkuCode(), ReceiptItemResponse::setSkuCode);
                mapping.map(entity -> entity.getProduct().getProductDetail().getName(), ReceiptItemResponse::setProductName);
            });
        
        modelMapper.typeMap(PatternColor.class, PatternColorItem.class)
                    .addMappings(mapping ->{
                        mapping.map(entity -> entity.getLensColor(), PatternColorItem::setLensColor);
                        mapping.map(entity -> entity.getLensColorName(), PatternColorItem::setLensColorName);
                        mapping.map(entity -> entity.getFrameColor(), PatternColorItem::setFrameColor);
                        mapping.map(entity -> entity.getFrameColorName(), PatternColorItem::setFrameColorName);
                    });
    }

}
