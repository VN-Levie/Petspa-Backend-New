package vn.aptech.petspa.dto;

import lombok.Data;
import java.util.List;

@Data
public class PetTypeDTO {

    private Long id = -1L;

    private String name;

    private String description;
}
