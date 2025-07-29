package shopdev.repository.imp;

import org.springframework.stereotype.Repository;

import shopdev.entity.ProductEntity;
import shopdev.repository.IProductRepository;

@Repository
public class ProductRepositoryImpl implements IProductRepository {
    @Override
    public ProductEntity createProduct(ProductEntity productEntity) {
        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.setId(1);
        productEntity1.setName("A");
        productEntity1.setPrice(100);
        return productEntity1;
    }

    @Override
    public ProductEntity[] getAllProduct() {
        return new ProductEntity[0];
    }
}
