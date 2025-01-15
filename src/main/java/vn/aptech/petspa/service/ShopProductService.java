package vn.aptech.petspa.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.aptech.petspa.dto.ShopCategoryDTO;
import vn.aptech.petspa.dto.ShopProductDTO;
import vn.aptech.petspa.entity.ShopCategory;
import vn.aptech.petspa.entity.ShopProduct;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.exception.NotFoundException;
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
            return shopProductRepository.findByNameAndCategoryId(name, categoryId, false, pageable);
        } else if (name != null) {
            return shopProductRepository.findByName(name, false, pageable);
        } else if (categoryId != null) {
            return shopProductRepository.findByCategoryId(categoryId, false, pageable);
        } else {
            return shopProductRepository.findAllUndeleted(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<ShopProductDTO> getShopProductsAdmin(String name, Long categoryId, Pageable pageable) {
        if (name != null && categoryId != null) {
            return shopProductRepository.findByNameAndCategoryId(name, categoryId, true, pageable);
        } else if (name != null) {
            return shopProductRepository.findByName(name, true, pageable);
        } else if (categoryId != null) {
            return shopProductRepository.findByCategoryId(categoryId, true, pageable);
        } else {
            return shopProductRepository.findAllAdmin(pageable);
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
            product.setDescription(productDTO.getDescription());
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
            product.setDescription(productDTO.getDescription());
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
                .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(shopCategoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found")));
        product.setDescription(productDTO.getDescription());
        shopProductRepository.save(product);
    }

    @Transactional
    public int deleteShopProduct(User user, Long productId) {
        ShopProduct product = shopProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getDeleted()) {
            shopProductRepository.unDelete(product.getId());
            return 0;
        }
        shopProductRepository.softDelete(product.getId());

        return 1;
    }

    public ShopProductDTO getShopProductById(Long productId) {
        Optional<ShopProduct> product = shopProductRepository.findById(productId);
        if (product.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        if (product.get().getDeleted()) {
            throw new NotFoundException("Product not found");
        }

        return new ShopProductDTO(product.get());
    }

    // get all categories
    public List<ShopCategoryDTO> retrieveCategories() {
        return shopCategoryRepository.findAllUndeleted();
    }

    // get all categories
    public List<ShopCategoryDTO> retrieveCategoriesAdmin() {
        return shopCategoryRepository.findAllAdmin();
    }

    // add category
    public void addCategory(ShopCategoryDTO categoryDTO) {
        // check if category already exists
        if (shopCategoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Category already exists");
        }
        ShopCategory category = new ShopCategory();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        shopCategoryRepository.save(category);
    }

    // update category
    public void updateCategory(ShopCategoryDTO categoryDTO) {

        // Tìm danh mục hiện tại
        ShopCategory category = shopCategoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        // Kiểm tra trùng lặp tên danh mục, loại trừ danh mục hiện tại
        shopCategoryRepository.findByName(categoryDTO.getName())
                .filter(existingCategory -> !existingCategory.getId().equals(category.getId()))
                .ifPresent(existingCategory -> {
                    throw new IllegalArgumentException("Category already exists");
                });

        // Cập nhật danh mục
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        // Lưu lại thay đổi
        shopCategoryRepository.save(category);
    }

    // delete category
    public void deleteCategory(Long categoryId) {
        ShopCategory category = shopCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        shopCategoryRepository.softDelete(category.getId());
    }

}
