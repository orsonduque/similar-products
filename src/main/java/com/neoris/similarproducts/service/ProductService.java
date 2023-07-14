package com.neoris.similarproducts.service;

import com.neoris.similarproducts.service.dto.ProductDetailDTO;
import com.neoris.similarproducts.web.rest.errors.ResourceNotFoundException;

import java.util.List;


public interface ProductService {

    /**
     * Get similar products by product id.
     *
     * @param productId the product id.
     * @return the list of similar products.
     */
    List<ProductDetailDTO> getSimilarProducts(String productId) throws ResourceNotFoundException;
}

