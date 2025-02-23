package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "address_books")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressBook extends BaseEntity {

    @Column(name = "freeform_address")
    private String freeformAddress;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", nullable = true)
    private String email;

    // Liên kết với bảng Account, sử dụng ManyToOne vì một địa chỉ chỉ thuộc về một tài khoản
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
