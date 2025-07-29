package shopdev.repository;

import shopdev.entity.ProductEntity;
public interface IProductRepository {
    ProductEntity createProduct(ProductEntity productEntity);
    ProductEntity[] getAllProduct();
}