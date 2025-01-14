package vn.aptech.petspa.dto;

import lombok.Data;
import vn.aptech.petspa.entity.AddressBook;
import vn.aptech.petspa.entity.User;

@Data
public class AddressBookDTO {
    private Long id;
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private Long userId;

    // New fields
    private String freeformAddress;
    private String streetNumber;
    private String province;
    private Double latitude;
    private Double longitude;
    private String name;
    private String phone;

    // Constructor
    public AddressBookDTO() {
    }

    public AddressBookDTO(Long id, String street, String city, String postalCode, String country, Long accountId,
            String freeformAddress, String streetNumber, String province, Double latitude, Double longitude,
            String name,
            String phone) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.userId = accountId;
        this.freeformAddress = freeformAddress;
        this.streetNumber = streetNumber;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.phone = phone;
    }

    public AddressBookDTO(AddressBook addressBook) {
        this.id = addressBook.getId();
        this.street = addressBook.getStreet();
        this.city = addressBook.getCity();
        this.postalCode = addressBook.getPostalCode();
        this.country = addressBook.getCountry();
        this.userId = addressBook.getUser().getId();
        this.freeformAddress = addressBook.getFreeformAddress();
        this.streetNumber = addressBook.getStreetNumber();
        this.province = addressBook.getProvince();
        this.latitude = addressBook.getLatitude();
        this.longitude = addressBook.getLongitude();
        this.name = addressBook.getName();
        this.phone = addressBook.getPhone();
    }

    // toString
    @Override
    public String toString() {
        return "AddressBookDTO [id=" + id + ", street=" + street + ", city=" + city
                + ", postalCode=" + postalCode
                + ", country=" + country + ", accountId=" + userId
                + ", freeformAddress=" + freeformAddress
                + ", streetNumber=" + streetNumber + ", province=" + province
                + ", latitude=" + latitude + ", longitude=" + longitude
                + ", name=" + name + ", phone=" + phone + "]";
    }

    public AddressBook toEntity(User user) {
        AddressBook addressBook = new AddressBook();
        addressBook.setId(this.id);
        if (addressBook.getId() == null || addressBook.getId() < 0) {
            addressBook.setId(null); // Đảm bảo Hibernate tạo ID mới
        }
        addressBook.setStreet(this.street);
        addressBook.setCity(this.city);
        addressBook.setPostalCode(this.postalCode);
        addressBook.setCountry(this.country);
        addressBook.setUser(user);
        addressBook.setFreeformAddress(this.freeformAddress);
        addressBook.setStreetNumber(this.streetNumber);
        addressBook.setProvince(this.province);
        addressBook.setLatitude(this.latitude);
        addressBook.setLongitude(this.longitude);
        addressBook.setName(this.name);
        addressBook.setPhone(this.phone);
        return addressBook;
    }

}
