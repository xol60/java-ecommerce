package shopdev.service;

import shopdev.entity.ProductEntity;

public interface IProductService {
    ProductEntity createProduct(ProductEntity productEntity);
    ProductEntity[] getAllProduct();
}