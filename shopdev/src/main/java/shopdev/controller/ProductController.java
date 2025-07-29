package shopdev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.modelmapper.ModelMapper;
import shopdev.entity.ProductEntity;
import shopdev.service.impl.ProductServiceImpl;

@RestController
@RequestMapping("/v1/api/product")
class ProductController {
     @Autowired
     private ProductServiceImpl productServiceImpl;
    // private ModelMapper modelMapper;

     @PostMapping("/createProduct")
     public ProductEntity createProduct(@RequestBody ProductEntity productEntity) {
         return productServiceImpl.createProduct(productEntity);
     }

     @GetMapping("/getAllProduct")
     public ProductEntity[] getAllProduct() {
         return productServiceImpl.getAllProduct();
     }
}