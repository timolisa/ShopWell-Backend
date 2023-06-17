package com.shopwell.api.service;

import com.shopwell.api.exceptions.ProductNotFoundException;
import com.shopwell.api.model.VOs.request.*;
import com.shopwell.api.model.VOs.response.ApiResponseVO;
import com.shopwell.api.model.VOs.response.ProductSearchResponseVO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ApiResponseVO<?> saveProduct(ProductRegistrationVO productRegistrationVO);

    ApiResponseVO<?> editProduct(Long id, ProductRegistrationVO productRegistrationVO) throws ProductNotFoundException;

    ApiResponseVO<?> getProduct(Long id) throws ProductNotFoundException;

    ApiResponseVO<ProductSearchResponseVO> searchProductsByCriteria(int pageNumber, int pageSize, ProductSearchRequestVO productSearchRequestVO);

    ApiResponseVO<?> deleteProduct(Long id);

    String addProductToCart(AddToCartRequestVO addToCartRequestVO);

    String removeProductFromCart(Long productId, Long customerId);

    List<CartItemVO> getCartItems(Long customerId);

    BigDecimal calculateTotalPrice(Long customerId);
}
