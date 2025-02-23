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

import vn.aptech.petspa.dto.AddressBookDTO;
import vn.aptech.petspa.dto.SpaCategoriesDTO;
import vn.aptech.petspa.dto.SpaProductDTO;
import vn.aptech.petspa.entity.AddressBook;
import vn.aptech.petspa.entity.SpaCategory;
import vn.aptech.petspa.entity.SpaProduct;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.exception.NotFoundException;
import vn.aptech.petspa.repository.AddressBookRepository;
import vn.aptech.petspa.repository.SpaCategoryRepository;
import vn.aptech.petspa.repository.SpaProductRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.util.JwtUtil;

@Service
public class UserExtendService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Transactional(readOnly = true)
    public Page<AddressBookDTO> getUserAddressBook(Long id, Pageable pageable) {
        return addressBookRepository.findByUserId(id, pageable);
    }

    public void addAddressBook(AddressBookDTO addressBookDTO) {
        User user = userRepository.findById(addressBookDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Kiểm tra trùng userId và freeformAddress
        AddressBook existingAddressBook = addressBookRepository
                .findByUserIdAndFreeformAddress(addressBookDTO.getUserId(), addressBookDTO.getFreeformAddress());

        // Nếu tồn tại bản ghi và không phải bản ghi hiện tại (trong trường hợp sửa)
        if (existingAddressBook != null && !existingAddressBook.getId().equals(addressBookDTO.getId())) {
            throw new NotFoundException("Address already exists");
        }

        addressBookRepository.save(addressBookDTO.toEntity(user));
    }

    public void deleteAddressBook(AddressBookDTO addressBookDTO) {
        // addressBookRepository.deleteById(addressBookDTO.getId());
    }
}
