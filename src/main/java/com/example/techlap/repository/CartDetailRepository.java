package com.example.techlap.repository;

import com.example.techlap.domain.Cart;
import com.example.techlap.domain.CartDetail;
import com.example.techlap.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    CartDetail findByCartAndProduct(Cart cart, Product product);
    int countByCart(Cart cart);
}
