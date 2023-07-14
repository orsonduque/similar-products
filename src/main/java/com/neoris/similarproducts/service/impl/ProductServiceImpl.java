package com.neoris.similarproducts.service.impl;

import com.neoris.similarproducts.service.ProductService;
import com.neoris.similarproducts.service.dto.ProductDetailDTO;
import com.neoris.similarproducts.service.dto.SimilarProductsDTO;
import com.neoris.similarproducts.web.rest.errors.ExceptionMessages;
import com.neoris.similarproducts.web.rest.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {

    private final RestTemplate restTemplate;

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final String externalServiceUrl;

    @Autowired
    public ProductServiceImpl(RestTemplate restTemplate,  @Value("${external.service.url}") String externalServiceUrl) {
        this.restTemplate = restTemplate;
        this.externalServiceUrl = externalServiceUrl;

    }

    public List<ProductDetailDTO> getSimilarProducts(String productId) throws ResourceNotFoundException {
        SimilarProductsDTO similarProductsDTO;
        List<ProductDetailDTO> response = new ArrayList<>();
        try {
            similarProductsDTO = restTemplate.getForObject(
                    String.format("%s/product/{productId}/similarids", externalServiceUrl),
                    SimilarProductsDTO.class, productId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);
        }
        if (similarProductsDTO != null) {

            List<CompletableFuture<ProductDetailDTO>> completableFutures = similarProductsDTO.stream()
                    .map(id -> CompletableFuture.supplyAsync(() -> getProductDetail(id)))
                    .toList();

            CompletableFuture<List<ProductDetailDTO>> allCompletableFuture = CompletableFuture.allOf(
                            completableFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> completableFutures.stream()
                            .filter(cf -> !cf.isCompletedExceptionally())
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .toList());

            response = allCompletableFuture.join();
        }
        return response;
    }

    private ProductDetailDTO getProductDetail(String id) {
        try {
            ResponseEntity<ProductDetailDTO> responseEntity = restTemplate.exchange(
                    String.format("%s/product/{productId}", externalServiceUrl),
                    HttpMethod.GET,
                    null,
                    ProductDetailDTO.class,
                    id);
            return responseEntity.getBody();
        } catch (Exception e) {
            log.debug(ExceptionMessages.PRODUCT_DETAIL_NOT_FOUND, id);
            return null;
        }
    }

}

