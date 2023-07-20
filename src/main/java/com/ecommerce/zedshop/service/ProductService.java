package com.ecommerce.zedshop.service;

import com.ecommerce.zedshop.model.Category;
import com.ecommerce.zedshop.model.Product;
import com.ecommerce.zedshop.model.dto.ProductDto;
import com.ecommerce.zedshop.repository.ProductRepository;
import com.ecommerce.zedshop.utils.ImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ProductService {
@Autowired
    private ProductRepository repo;

    @Autowired
    private ImageUpload imageUpload;
    public void  saveProductToDB(MultipartFile file, String name, String description
            , double price, int currentQuantity ,Category category)
    {
        Product p = new Product();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if(fileName.contains(".."))
        {
            System.out.println("not a a valid file");
        }
        try {
            p.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.setDescription(description);

        p.setName(name);
        p.setCostPrice(price);
        p.setCurrentQuantity(currentQuantity);
        p.setCategory(category);
        p.set_activated(true);
        p.set_deleted(false);

        repo.save(p);
    }


    public Product update (ProductDto productDto) {
        try {
            Product product = repo.getById(productDto.getId());

            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setSalePrice(productDto.getSalePrice());
            product.setCostPrice(productDto.getCostPrice());
            product.setCurrentQuantity(productDto.getCurrentQuantity());
            product.setCategory(productDto.getCategory());
            return repo.save(product);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }



    public ProductDto getById(Long id) {
        Product product = repo.getById(id);
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        productDto.setCategory(product.getCategory());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setImage(product.getImage());
        productDto.setDeleted(product.is_deleted());
        productDto.setActivated(product.is_activated());
        return productDto;
    }


    public void deleteById(Long id) {
        Product product = repo.getById(id);
        product.set_deleted(true);
        product.set_activated(false);
        repo.save(product);
    }

    public List<Product> getAllProduct()
    {
        return repo.findAll();
    }

    public void enableById(Long id) {
        Product product = repo.getById(id);
        product.set_activated(true);
        product.set_deleted(false);
        repo.save(product);
    }


    /*public void changeProductName(Long id,String name){
        Product product=new Product();
        product=repo.findById(id).get();
        product.setName(name);
        repo.save(product);
    }*/

   /* public void changeDescription(Long id,String description ){
        Product product=new Product();
        product=repo.findById(id).get();
        product.setDescription(description);
        repo.save(product);
    }

    public void changePrice(Long id,Double price){
        Product product=new Product();
        product=repo.findById(id).get();
        product.setCostPrice(price);
    }*/
    public void deleteProductById(Long id){
        repo.deleteById(id);
    }

    public Product getProductById(Long id) {
        return repo.getById(id);
    }

    public List<Product> getProductsInCategory(Long categoryId) {
        return repo.getProductsInCategory(categoryId);
    }

    public List<ProductDto> searchProducts(String keyword) {
        return transferData(repo.searchProducts(keyword));
    }

    private List<ProductDto> transferData(List<Product> products) {
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setDescription(product.getDescription());
            productDto.setImage(product.getImage());
            productDto.setCategory(product.getCategory());
            productDto.setActivated(product.is_activated());
            productDto.setDeleted(product.is_deleted());
            productDtos.add(productDto);
        }
        return productDtos;
    }

}
