package com.CRM.configuration.mapper.ReceivePO;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.CRM.model.PurchaseOrder;
import com.CRM.model.PurchaseOrderColor;
import com.CRM.model.PurchaseOrderItem;
import com.CRM.response.ReceiveDelivery.POReceiveColorResponse;
import com.CRM.response.ReceiveDelivery.POReceiveInfoResponse;
import com.CRM.response.ReceiveDelivery.POReceiveItemResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;




@Component
@RequiredArgsConstructor
public class ReceivePOMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setup() {
        // Dùng emptyTypeMap để tránh ModelMapper tự động map các thuộc tính con trước khi addMappings
        // 1. PurchaseOrder -> POReceiveInfoResponse
        modelMapper.emptyTypeMap(PurchaseOrder.class, POReceiveInfoResponse.class)
                .addMappings(mapping -> {
                    mapping.map(entity -> entity.getId(), POReceiveInfoResponse::setId);
                    mapping.map(entity -> entity.getName(), POReceiveInfoResponse::setName);
                    mapping.map(entity -> entity.getSupplier().getName(), POReceiveInfoResponse::setSupplierName);
                    mapping.map(entity -> entity.getWarehouse().getName(), POReceiveInfoResponse::setWarehouseName);
                    mapping.map(entity -> entity.getOrderDate(), POReceiveInfoResponse::setOrderDate);
                    mapping.map(entity -> entity.getExpectedDeliveryDate(), POReceiveInfoResponse::setExpectedDeliveryDate);
                    mapping.map(entity -> entity.getNote(), POReceiveInfoResponse::setNote);
                    mapping.skip(POReceiveInfoResponse::setItems);
                }).implicitMappings();

        // 2. PurchaseOrderItem -> POReceiveItemResponse
        modelMapper.emptyTypeMap(PurchaseOrderItem.class, POReceiveItemResponse.class)
                .addMappings(mapping -> {
                    mapping.map(entity -> entity.getId(), POReceiveItemResponse::setId);
                    mapping.map(entity -> entity.getProduct().getId(), POReceiveItemResponse::setProductId);
                    mapping.map(entity -> entity.getProduct().getProductDetail().getName(), POReceiveItemResponse::setProductName);
                    mapping.map(entity -> entity.getProduct().getSkuCode(), POReceiveItemResponse::setSkuCode);
                    mapping.map(entity -> entity.getUnitPrice(), POReceiveItemResponse::setUnitPrice);
                    mapping.map(entity -> entity.getTaxRate(), POReceiveItemResponse::setTaxRate);
                    mapping.map(entity -> entity.isFullyReceived(), POReceiveItemResponse::setFullyReceived);

                    // Dùng Converter để fix lỗi "Integer is not an instance of PurchaseOrderItem"
                    mapping.using(ctx -> ((PurchaseOrderItem) ctx.getSource()).getQuantityOrdered())
                           .map(entity -> entity, POReceiveItemResponse::setSumQuantityOrdered);

                    mapping.using(ctx -> ((PurchaseOrderItem) ctx.getSource()).getQuantityReceived())
                           .map(entity -> entity, POReceiveItemResponse::setSumQuantityReceived);

                    mapping.using(ctx -> {
                        PurchaseOrderItem item = (PurchaseOrderItem) ctx.getSource();
                        return item.getQuantityOrdered() - item.getQuantityReceived();
                    }).map(entity -> entity, POReceiveItemResponse::setRemainingQuantity);

                    mapping.skip(POReceiveItemResponse::setColors);
                }).implicitMappings();

        // 3. PurchaseOrderColor -> POReceiveColorResponse
        modelMapper.emptyTypeMap(PurchaseOrderColor.class, POReceiveColorResponse.class)
                .addMappings(mapping -> {
                    mapping.map(entity -> entity.getId(), POReceiveColorResponse::setId);
                    mapping.map(entity -> entity.getQuantityOrdered(), POReceiveColorResponse::setQuantityOrdered);
                    mapping.map(entity -> entity.getQuantityReceived(), POReceiveColorResponse::setQuantityReceived);
                    mapping.map(entity -> entity.isFullyReceived(), POReceiveColorResponse::setFullyReceived);

                    // Dùng Converter cho phép tính trừ để an toàn tuyệt đối
                    mapping.using(ctx -> {
                        PurchaseOrderColor color = (PurchaseOrderColor) ctx.getSource();
                        return color.getQuantityOrdered() - color.getQuantityReceived();
                    }).map(entity -> entity, POReceiveColorResponse::setRemainingQuantity);

                    mapping.skip(POReceiveColorResponse::setPatternColor);
                }).implicitMappings();
    }
}