package com.teamketo.shop.service;

import com.teamketo.shop.entity.Product;
import com.teamketo.shop.repository.ProductRepository;
import com.teamketo.shop.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/products";

    //상품 등록
    public Product insertProduct(Product product, MultipartFile image) throws Exception {
        if(image != null && !image.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(image, uploadDir);
            product.setImageUrl(fileName);
        }
        return productRepository.save(product);
    }

    //상품 수정
    public Product updateProduct(Long id, Product product, MultipartFile image) throws Exception {
        Product pro = productRepository.findById(id).orElseThrow();
        pro.setName(product.getName());
        pro.setPrice(product.getPrice());
        pro.setDescription(product.getDescription());
        pro.setStock(product.getStock());
        pro.setCategoryId(product.getCategoryId());
        if(image != null && !image.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(image, uploadDir);
            pro.setImageUrl(fileName);
        }
        return productRepository.save(pro);
    }
}