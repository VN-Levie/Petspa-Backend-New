package vn.aptech.petspa.dto;
import lombok.Data;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.PetHealth;

import java.util.List;

@Data
public class PaymentDTO {
    private Long id;
    private Long amount;
    private String bankCode;
    private String language;
    private String detail;
    private String ip;
}
