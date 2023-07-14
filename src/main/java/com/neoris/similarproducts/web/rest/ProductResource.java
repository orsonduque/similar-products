package com.neoris.similarproducts.web.rest;

import com.neoris.similarproducts.service.ProductService;
import com.neoris.similarproducts.service.dto.ProductDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ProductResource {

    private final ProductService productService;

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);
    @Autowired
    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    /**
     * {@code GET  /product/{productId}/similar} : get similar products by productId.
     *
     * @param productId product id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the similar products in body.
     */
    @GetMapping("/product/{productId}/similar")
    public ResponseEntity<List<ProductDetailDTO>> getSimilarProducts(@PathVariable("productId") String productId) {
        log.debug("request similar products by id:{}",productId );
        return ResponseEntity.ok().body(productService.getSimilarProducts(productId));
    }
}

