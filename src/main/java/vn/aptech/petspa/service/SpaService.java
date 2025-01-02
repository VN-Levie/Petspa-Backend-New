package vn.aptech.petspa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.aptech.petspa.dto.SpaCategoriesDTO;
import vn.aptech.petspa.repository.SpaCategoryRepository;
import vn.aptech.petspa.repository.SpaProductRepository;
import vn.aptech.petspa.util.JwtUtil;

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
        return spaCategoryRepository.findAll().stream().map(spaCategory -> {
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

}
