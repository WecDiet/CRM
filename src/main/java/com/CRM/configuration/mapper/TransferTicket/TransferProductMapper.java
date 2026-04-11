package com.CRM.configuration.mapper.TransferTicket;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.CRM.model.PatternColor;
import com.CRM.model.Product;
import com.CRM.response.PatternColor.PatternColorItem;
import com.CRM.response.TransferTicket.TransferProductResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferProductMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setup() {
        modelMapper.typeMap(Product.class, TransferProductResponse.class)
            .addMappings(mapping -> {
                mapping.map(entity -> entity.getId(), TransferProductResponse::setId);
                mapping.map(entity -> entity.getProductDetail().getName(), TransferProductResponse::setProductName);
                mapping.map(entity -> entity.getSkuCode(), TransferProductResponse::setSkuCode);
                mapping.map(entity -> entity.getProductDetail().getColors(), TransferProductResponse::setColors);
            });

        modelMapper.typeMap(PatternColor.class, PatternColorItem.class)
            .addMappings(mapping ->{
                        mapping.map(entity -> entity.getId(), PatternColorItem::setId);
                        mapping.map(entity -> entity.getLensColor(), PatternColorItem::setLensColor);
                        mapping.map(entity -> entity.getLensColorName(), PatternColorItem::setLensColorName);
                        mapping.map(entity -> entity.getFrameColor(), PatternColorItem::setFrameColor);
                        mapping.map(entity -> entity.getFrameColorName(), PatternColorItem::setFrameColorName);
                    });
    }
}
