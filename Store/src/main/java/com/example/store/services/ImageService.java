package com.example.store.services;

import com.example.store.model.Image;
import com.example.store.model.Product;
import com.example.store.repository.ImageRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class ImageService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    public ImageService(ProductRepository productRepository, ImageRepository imageRepository, S3Service s3Service) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.s3Service = s3Service;
    }

    public void deleteImagesByProduct(Product product) {
        if (!product.getImages().isEmpty()) {
            for (Image image : product.getImages()) {
                s3Service.deleteObject(
                        "myapp2",
                        "products/" + image.getName());
            }
            product.setImages(new ArrayList<>());
            imageRepository.deleteAll(product.getImages());
        }
    }

    public void addMultipleFiles(MultipartFile[] files, Product product) {
        for (MultipartFile file : files) {
            try {
                UUID uuid = UUID.randomUUID();
                s3Service.putObject(
                        "myapp2",
                        "products/" + uuid,
                        file.getBytes());
                Image image = new Image(uuid.toString());
                imageRepository.save(image);
                product.addImage(image);
            } catch (IOException ignored) {

            }
        }
        productRepository.save(product);
    }
}
