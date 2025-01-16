package vn.aptech.petspa.dto;



import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestDayDTO {

    @NotBlank(message = "reason is required")
    @Size(max = 50)
    private String reason;

    @NotBlank(message = "date is required")
    private Date date;
}
