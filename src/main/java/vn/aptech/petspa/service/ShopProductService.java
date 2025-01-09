package vn.aptech.petspa.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.aptech.petspa.dto.ShopProductDTO;
import vn.aptech.petspa.entity.ShopCategory;
import vn.aptech.petspa.entity.ShopProduct;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.ShopCategoryRepository;
import vn.aptech.petspa.repository.ShopProductRepository;


@Service
public class ShopProductService {

    @Autowired
    private ShopProductRepository shopProductRepository;

    @Autowired
    private ShopCategoryRepository shopCategoryRepository;

    @Autowired
    private FileService fileService;

    @Transactional(readOnly = true)
    public Page<ShopProductDTO> getShopProducts(String name, Long categoryId, Pageable pageable) {
        if (name != null && categoryId != null) {
            return shopProductRepository.findByNameAndCategoryId(name, categoryId, pageable);
        } else if (name != null) {
            return shopProductRepository.findByName(name, pageable);
        } else if (categoryId != null) {
            return shopProductRepository.findByCategoryId(categoryId, pageable);
        } else {
            return shopProductRepository.findAll(pageable);
        }
    }

    @Transactional
    public void addShopProduct(User user, ShopProductDTO productDTO, MultipartFile file) {
        try {
            if (!fileService.isImageSize(file.getSize())) {
                throw new IllegalArgumentException("File size exceeds the allowed limit.");
            }
            if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid image format.");
            }

            String uploadDir = "shop-products";
            String fileUrl = fileService.uploadFile(file, uploadDir);

            ShopCategory category = shopCategoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            ShopProduct product = new ShopProduct();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setImageUrl(fileUrl);
            product.setCategory(category);

            shopProductRepository.save(product);

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public void editShopProductWithImage(User user, ShopProductDTO productDTO, MultipartFile file) {
        try {
            ShopProduct product = shopProductRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (!fileService.isImageSize(file.getSize())) {
                throw new IllegalArgumentException("File size exceeds the allowed limit.");
            }
            if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid image format.");
            }

            String uploadDir = "shop-products";
            String fileUrl = fileService.uploadFile(file, uploadDir);

            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setImageUrl(fileUrl);
            product.setCategory(shopCategoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found")));

            shopProductRepository.save(product);

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public void editShopProductWithoutImage(User user, ShopProductDTO productDTO) {
        ShopProduct product = shopProductRepository.findById(productDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(shopCategoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found")));

        shopProductRepository.save(product);
    }

    @Transactional
    public void deleteShopProduct(User user, ShopProductDTO productDTO) {
        ShopProduct product = shopProductRepository.findById(productDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        shopProductRepository.softDelete(product.getId());
    }
}
