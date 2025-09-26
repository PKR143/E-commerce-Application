package com.java.jwt.service;

import com.java.jwt.dto.CartRequest;
import com.java.jwt.dto.CartResponse;
import com.java.jwt.dto.GeneralResponse;
import com.java.jwt.dto.Response;
import com.java.jwt.entity.ProductDetailsEntity;
import com.java.jwt.entity.User;
import com.java.jwt.entity.UserCartDetailsEntity;
import com.java.jwt.exception.PaymentException;
import com.java.jwt.repository.CartRepository;
import com.java.jwt.repository.ProductRepository;
import com.java.jwt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    private static  final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Override
    public ResponseEntity<?> addToCart(CartRequest cart) {
        logger.info("add to cart request: {}",cart);

        try{


            if(cart.getUsername() == null || cart.getUsername().isEmpty()){
                logger.info("username is missing in the request");
                throw new PaymentException("Username is missing in the request");
            }
            if(cart.getProductId() == null){
                logger.info("product id is missing in the request");
                throw new PaymentException("product id is missing in the request");
            }

//            if(cart.getQuantity() == null){
//                cart.setQuantity(1L);
//            }

            if(userRepository.findById(cart.getUsername()).isEmpty()){
                logger.info("Username not exists in db");
                throw new PaymentException("Username not exists.");
            }

            Optional<ProductDetailsEntity> productDetailsEntityOpt = productRepository.findById(cart.getProductId());

            if(productDetailsEntityOpt.isEmpty()){
                logger.info("Product not exists in db");
                throw new PaymentException("No Such product exists.");
            }
            ProductDetailsEntity productEntity = productDetailsEntityOpt.get();

            Optional<UserCartDetailsEntity> cartOpt = cartRepository.findById(cart.getUsername());
            UserCartDetailsEntity  cartEntity;
           List<ProductDetailsEntity> list = new ArrayList<>();
            Double price = 0D;
            if(cartOpt.isPresent()){
                //cart already created
                cartEntity = cartOpt.get();
                list = cartEntity.getItems();


                for(ProductDetailsEntity product: list){
                    if(product.getProductId().equals(cart.getProductId())){
                        logger.info("Product is already present in DB");
                        return ResponseEntity.ok(new GeneralResponse(mapToCartResponse(cart.getUsername(), productEntity),1L, "Item is already present in Cart."));
                    }
                }
                 if(cartEntity.getAmount() != null){
                     price = cartEntity.getAmount();
                 }
            }else{
                //new cart creation
                cartEntity = new UserCartDetailsEntity();
                cartEntity.setUsername(cart.getUsername());
            }
            //add item to cart
            price += productEntity.getPrice();
            int priceInt = (int) (price * 100);
            price = (double) priceInt/100;
            cartEntity.setAmount(price);
            list.add(productEntity);
            //Save to db
            cartRepository.save(cartEntity);
            logger.info(cart.getProductId()+" Item added to cart");
            return ResponseEntity.status(HttpStatus.CREATED).body(new GeneralResponse(mapToCartResponse(cart.getUsername(),productEntity),1L,"Item added to cart."));
        }catch(PaymentException e){
            throw e;
        }
        catch (Exception e) {
            logger.info("Exception @addToCart due to: {}",e.getMessage());
            throw e;
        }

    }

    private Response mapToCartResponse(String username, ProductDetailsEntity productEntity) {
        return CartResponse.builder()
                .username(username)
                .productId(productEntity.getProductId())
                .productName(productEntity.getProductName())
                .productDescription(productEntity.getProductDescription())
                .productSize(productEntity.getProductSize())
                .productColour(productEntity.getProductColour())
                .stockAvailable(productEntity.getStockAvailable())
                .price(productEntity.getPrice())
                .build();
    }

    @Override
    public ResponseEntity<?> getCartItems(String username) {
        logger.info("fetching cart items for user: {}",username);
        try{

            if(username == null || username.isEmpty()){
                logger.info("username is missing in the request");
                throw new PaymentException("Username is missing in the request");
            }

            Optional<UserCartDetailsEntity> cartOpt = cartRepository.findById(username);
            if(cartOpt.isEmpty()){
                logger.info("username has not been created for the username: {}",username);
                return ResponseEntity.ok(new GeneralResponse(null, 1L, "Empty Cart"));

            }
            UserCartDetailsEntity cartEntity = cartOpt.get();
            return ResponseEntity.ok(cartEntity);
        }catch(PaymentException e){
            throw e;
        }catch(Exception e){
            logger.info("Exception @getCartItems due to : {}",e.getMessage());
            throw e;
        }
    }
}
