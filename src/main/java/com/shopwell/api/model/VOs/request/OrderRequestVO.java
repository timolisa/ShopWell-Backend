package com.shopwell.api.model.VOs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderRequestVO {

    private String shippingAddress;

    private String paymentMethod;
}
