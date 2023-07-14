package com.neoris.similarproducts.service.impl;

import com.neoris.similarproducts.service.dto.ProductDetailDTO;
import com.neoris.similarproducts.service.dto.SimilarProductsDTO;
import com.neoris.similarproducts.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private ProductServiceImpl productService;

    private final String externalServiceUrl = "http://localhost:3001";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(restTemplate, externalServiceUrl);
    }

    @Test
    void testGetSimilarProducts_WithValidProductId_ReturnsProductList() throws ResourceNotFoundException {
        String productId = "123";
        SimilarProductsDTO similarProductsDTO = new SimilarProductsDTO();
        similarProductsDTO.add("456");
        similarProductsDTO.add("789");

        ProductDetailDTO productDetailDTO1 = new ProductDetailDTO();
        productDetailDTO1.setId("456");
        ProductDetailDTO productDetailDTO2 = new ProductDetailDTO();
        productDetailDTO2.setId("789");

        when(restTemplate.getForObject(
                String.format("%s/product/{productId}/similarids", externalServiceUrl),
                SimilarProductsDTO.class, productId)).thenReturn(similarProductsDTO);

        when(restTemplate.exchange(
                String.format("%s/product/{productId}", externalServiceUrl),
                HttpMethod.GET,
                null,
                ProductDetailDTO.class,
                "456")).thenReturn(ResponseEntity.ok(productDetailDTO1));

        when(restTemplate.exchange(
                String.format("%s/product/{productId}", externalServiceUrl),
                HttpMethod.GET,
                null,
                ProductDetailDTO.class,
                "789")).thenReturn(ResponseEntity.ok(productDetailDTO2));

        List<ProductDetailDTO> result = productService.getSimilarProducts(productId);

        assertEquals(2, result.size());
        assertEquals("456", result.get(0).getId());
        assertEquals("789", result.get(1).getId());
    }

    @Test
    void testGetSimilarProducts_WithNotFoundProduct_ThrowsResourceNotFoundException() {

        String productId = "123";

        when(restTemplate.getForObject(
                String.format("%s/product/{productId}/similarids", externalServiceUrl),
                SimilarProductsDTO.class, productId)).thenThrow(HttpClientErrorException.NotFound.class);

        assertThrows(ResourceNotFoundException.class, () -> productService.getSimilarProducts(productId));
    }

    @Test
    void testGetSimilarProducts_WithNullSimilarProducts_ReturnsEmptyList() throws ResourceNotFoundException {
        String productId = "123";

        when(restTemplate.getForObject(
                String.format("%s/product/{productId}/similarids", externalServiceUrl),
                SimilarProductsDTO.class, productId)).thenReturn(null);

        List<ProductDetailDTO> result = productService.getSimilarProducts(productId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
