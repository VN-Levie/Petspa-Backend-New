package vn.aptech.petspa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyDTO {
    private Long userId;
    private String email = "";
    private boolean verified = false;

    public VerifyDTO() {
    }

    public VerifyDTO(String email, boolean verified) {
        this.email = email;
        this.verified = verified;
    }

    public VerifyDTO(Long userId, String email, boolean verified) {
        this.userId = userId;
        this.email = email;
        this.verified = verified;
    }
}
