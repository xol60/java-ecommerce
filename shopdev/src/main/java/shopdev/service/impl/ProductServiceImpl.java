package shopdev.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shopdev.entity.ProductEntity;
import shopdev.repository.imp.ProductRepositoryImpl;
import shopdev.service.IProductService;

@Service
public class ProductServiceImpl implements  IProductService{
    @Autowired
    private ProductRepositoryImpl productRepositoryImpl;
    public ProductEntity createProduct (ProductEntity productEntity) {
        return productRepositoryImpl.createProduct(productEntity);
    }

    public ProductEntity[] getAllProduct() {
        return productRepositoryImpl.getAllProduct();
    }
}