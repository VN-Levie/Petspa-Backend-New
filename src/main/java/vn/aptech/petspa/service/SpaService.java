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

import vn.aptech.petspa.dto.SpaCategoriesDTO;
import vn.aptech.petspa.dto.SpaProductDTO;
import vn.aptech.petspa.entity.SpaCategory;
import vn.aptech.petspa.entity.SpaProduct;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.exception.NotFoundException;
import vn.aptech.petspa.repository.SpaCategoryRepository;
import vn.aptech.petspa.repository.SpaProductRepository;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.ZDebug;

@Service
public class SpaService {

    @Autowired
    private SpaCategoryRepository spaCategoryRepository;

    @Autowired
    private SpaProductRepository spaProductRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private JwtUtil jwtUtil;

    public List<SpaCategoriesDTO> getAllServicesDTO() {
        return spaCategoryRepository.findAllUndeleted().stream().map(spaCategory -> {

            SpaCategoriesDTO spaCategoriesDTO = new SpaCategoriesDTO();
            spaCategoriesDTO.setId(spaCategory.getId());
            spaCategoriesDTO.setName(spaCategory.getName());
            spaCategoriesDTO.setDescription(spaCategory.getDescription());
            spaCategoriesDTO.setImageUrl(spaCategory.getImageUrl());
            spaCategoriesDTO.setItems(spaProductRepository.findByCategoryId(spaCategory.getId()));
            return spaCategoriesDTO;
        }).toList();
    }

    public Long countCategories() {
        return spaCategoryRepository.count();
    }

    public Long countProducts() {
        return spaProductRepository.count();
    }

    public Optional<List<SpaProductDTO>> getServicesByCategory(Long categoryId) {
        return spaProductRepository.findByCategoryId(categoryId);
    }

    public Optional<SpaProductDTO> getServicesById(Long id) {
        return spaProductRepository.findByIdAndDeletedFalse(id);
    }

    @Transactional(readOnly = true)
    public Page<SpaProductDTO> getSpaProducts(String name, Long categoryId, Pageable pageable) {
        if (name != null && categoryId != null) {
            return spaProductRepository.findByNameAndCategoryIdAdmin(name, categoryId, pageable);
        } else if (name != null) {
            return spaProductRepository.findByNameAdmin(name, pageable);
        } else if (categoryId != null) {
            return spaProductRepository.findByCategoryIdAdmin(categoryId, pageable);
        } else {
            return spaProductRepository.findAllAdmin(pageable);
        }
    }

    @Transactional
    public void addSpaProduct(User user, SpaProductDTO productDTO, MultipartFile file) {
        try {
            if (!fileService.isImageSize(file.getSize())) {
                throw new IllegalArgumentException("File size exceeds the allowed limit.");
            }
            if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid image format.");
            }

            String uploadDir = "spa-products";
            String fileUrl = fileService.uploadFile(file, uploadDir);

            SpaCategory category = spaCategoryRepository.findById(productDTO.getCategory())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            SpaProduct product = new SpaProduct();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setImageUrl(fileUrl);
            product.setDescription(productDTO.getDescription());
            product.setCategory(category);

            spaProductRepository.save(product);

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public void editSpaProductWithImage(User user, SpaProductDTO productDTO, MultipartFile file) {
        try {
            SpaProduct product = spaProductRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (!fileService.isImageSize(file.getSize())) {
                throw new IllegalArgumentException("File size exceeds the allowed limit.");
            }
            if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid image format.");
            }

            String uploadDir = "spa-products";
            String fileUrl = fileService.uploadFile(file, uploadDir);

            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setImageUrl(fileUrl);
            product.setDescription(productDTO.getDescription());
            SpaCategory category = spaCategoryRepository.findById(productDTO.getCategory())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            product.setCategory(category);

            spaProductRepository.save(product);

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public void editSpaProductWithoutImage(User user, SpaProductDTO productDTO) {
        if (productDTO == null || productDTO.getId() == null) {
            throw new IllegalArgumentException("Product data or ID must not be null.");
        }
        try {

            SpaProduct product = spaProductRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new NotFoundException("Product not found for ID: " + productDTO.getId()));

            // Cập nhật thông tin
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setDescription(productDTO.getDescription());

            // Tìm category
            SpaCategory category = spaCategoryRepository.findById(productDTO.getCategory())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Category not found for ID: " + productDTO.getCategory()));
            ZDebug.gI().ZigDebug("Tìm thấy Category ");
            product.setCategory(category);

            // Lưu sản phẩm vào database
            spaProductRepository.save(product);
        } catch (NotFoundException | IllegalArgumentException e) {
            // Xử lý ngoại lệ và ghi log
            ZDebug.gI().ZigDebug("Error updating product: " + e.getMessage());
            throw e; // Ném lại ngoại lệ để hệ thống xử lý rollback
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @Transactional
    public int deleteSpaProduct(User user, SpaProductDTO productDTO) {
        SpaProduct product = spaProductRepository.findById(productDTO.getId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getDeleted()) {
            spaProductRepository.unDelete(product.getId());
            return 0;
        }
        spaProductRepository.softDelete(product.getId());
        return 1;
    }

    public SpaProductDTO getSpaProductById(Long productId) {
        Optional<SpaProduct> product = spaProductRepository.findById(productId);
        if (product.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        if (product.get().getDeleted()) {
            throw new NotFoundException("Product not found");
        }

        return new SpaProductDTO(product.get());
    }

    // get all categories
    public List<SpaCategoriesDTO> retrieveCategories() {
        return spaCategoryRepository.findAllUndeleted();
    }

    // get all categories
    public List<SpaCategoriesDTO> retrieveCategoriesAdmin() {
        return spaCategoryRepository.findAllAdmin();
    }

    // add category
    public void addCategory(SpaCategoriesDTO categoryDTO, MultipartFile file) {
        // check null name
        try {
            if (categoryDTO.getName() == null) {
                throw new IllegalArgumentException("Category name is required");
            }

            // check null description
            if (categoryDTO.getDescription() == null) {
                throw new IllegalArgumentException("Category description is required");
            }

            if (!fileService.isImageSize(file.getSize())) {
                throw new IllegalArgumentException("File size exceeds the allowed limit.");
            }
            if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid image format.");
            }

            String uploadDir = "shop-categories";
            String fileUrl = fileService.uploadFile(file, uploadDir);

            // check if category already exists
            if (spaCategoryRepository.findByName(categoryDTO.getName()).isPresent()) {
                throw new IllegalArgumentException("Category already exists");
            }
            SpaCategory category = new SpaCategory();
            category.setName(categoryDTO.getName());
            category.setImageUrl(fileUrl);
            category.setDescription(categoryDTO.getDescription());

            spaCategoryRepository.save(category);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // update category
    public void updateCategory(SpaCategoriesDTO categoryDTO, MultipartFile file) {

        // Tìm danh mục hiện tại
        SpaCategory category = spaCategoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        // Kiểm tra trùng lặp tên danh mục, loại trừ danh mục hiện tại
        spaCategoryRepository.findByName(categoryDTO.getName())
                .filter(existingCategory -> !existingCategory.getId().equals(category.getId()))
                .ifPresent(existingCategory -> {
                    throw new IllegalArgumentException("Category already exists");
                });
        if (file != null && !file.isEmpty()) {
            try {
                if (!fileService.isImageSize(file.getSize())) {
                    throw new IllegalArgumentException("File size exceeds the allowed limit.");
                }
                if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                    throw new IllegalArgumentException("Invalid image format.");
                }

                String uploadDir = "shop-categories";
                String fileUrl = fileService.uploadFile(file, uploadDir);
                category.setImageUrl(fileUrl);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        // Cập nhật danh mục
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        // Lưu lại thay đổi
        spaCategoryRepository.save(category);
    }

    // delete category
    public int deleteCategory(Long categoryId) {
        SpaCategory category = spaCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (category.getDeleted()) {
            spaCategoryRepository.unDelete(category.getId());
            return 0;
        }
        spaCategoryRepository.softDelete(category.getId());
        return 1;
    }

}
