package com.shopwell.api.services.implementations;

import com.shopwell.api.exceptions.CustomerNotFoundException;
import com.shopwell.api.exceptions.ProductOutOfStockException;
import com.shopwell.api.model.VOs.request.AddToCartRequestVO;
import com.shopwell.api.model.VOs.response.CartItemResponseVO;
import com.shopwell.api.model.entity.*;
import com.shopwell.api.repository.CartItemRepository;
import com.shopwell.api.repository.CartRepository;
import com.shopwell.api.repository.CustomerRepository;
import com.shopwell.api.repository.ProductRepository;
import com.shopwell.api.services.CartService;
import com.shopwell.api.utils.MapperUtils;
import com.shopwell.api.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final MapperUtils mapperUtils;

    @Override
    public String addProductToCart(AddToCartRequestVO addToCartRequestVO) throws CustomerNotFoundException {

        Customer customer = UserUtils.getAuthenticatedUser(Customer.class);

        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Product product = productRepository.findById(addToCartRequestVO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantityAvailable() >= addToCartRequestVO.getQuantity()) {
            Cart cart = getOrCreateCart(customer.getEmail());
            CartItem cartItem = getCartItemByProductAndCart(product, cart);

            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(cart);
                cartItem.setQuotedPrice(BigDecimal.valueOf(product.getProductPrice()));
                cartItem.setQuantityOrdered(addToCartRequestVO.getQuantity());
            } else {
                cartItem.setQuantityOrdered(cartItem.getQuantityOrdered() + addToCartRequestVO.getQuantity());
            }
            cartItemRepository.save(cartItem);
        } else {
            throw new ProductOutOfStockException("Product is out of stock");
        }
        return "Product added to cart successfully";
    }

    @Override
    public void removeProductFromCart(Long productId) throws CustomerNotFoundException {
        Customer customer = UserUtils.getAuthenticatedUser(Customer.class);

        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Cart cart = getOrCreateCart(customer.getEmail());

        Product foundProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = getCartItemByProductAndCart(foundProduct, cart);

        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
        }
    }

    @Override
    public List<CartItemResponseVO> getCartItems() throws CustomerNotFoundException {
        Customer customer = UserUtils.getAuthenticatedUser(Customer.class);

        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Cart cart = getOrCreateCart(customer.getEmail());
        List<CartItem> cartItems = cart.getCartItems();

        return cartItems.stream()
                .map(mapperUtils::cartItemToCartItemResponseVO)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateTotalPrice(String email) {
        Cart cart = getOrCreateCart(email);
        List<CartItem> cartItems = cart.getCartItems();

        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            Double productPrice = cartItem.getProduct().getProductPrice();
            int quantity = cartItem.getQuantityOrdered();
            double itemPrice = productPrice * quantity;
            totalPrice = totalPrice + itemPrice;
        }

        return totalPrice;
    }

    private CartItem getCartItemByProductAndCart(Product product, Cart cart) {
        return cartItemRepository.findByProductAndCart(product, cart);
    }

    private Cart getOrCreateCart(String email) {
        Customer foundCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return cartRepository.findByCustomerId(foundCustomer.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomer(foundCustomer);
                    return cartRepository.save(cart);
                });
    }
}
