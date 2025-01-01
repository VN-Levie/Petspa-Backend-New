package vn.aptech.petspa.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean verified;
    private String token;
    private String refreshToken;

    public UserDTO() {
    }

    public UserDTO(Long id) {
        this.id = id;
    }

}
