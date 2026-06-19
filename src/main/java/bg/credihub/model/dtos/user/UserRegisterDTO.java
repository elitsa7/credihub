package bg.credihub.model.dtos.user;

import jakarta.validation.constraints.Email;
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
public class UserRegisterDTO {
    @NotBlank(message = "First name is required.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    private String lastName;
    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Phone number must contain 10 digits.")
    private String phoneNumber;
    @NotBlank(message = "Identification number is required.")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Identification number must contain 10 digits.")
    private String identificationNumber;
    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    private String email;
    @NotBlank(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
            message = "Password must contain at least 8 characters. one uppercase letter, one lowercase, one digit and one special symbol.")
    private String password;
    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;
}
