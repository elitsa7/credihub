package bg.credihub.model.dtos.user;

import bg.credihub.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    @NotBlank(message = "First name is required.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    private String lastName;
    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Phone number must contain 10 digits.")
    private String phoneNumber;
    private String email;
    private String identificationNumber;
    private Role role;
}
