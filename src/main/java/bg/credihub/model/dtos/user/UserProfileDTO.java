package bg.credihub.model.dtos.user;

import bg.credihub.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String identificationNumber;
    private String maskedIdentificationNumber;
    private Role role;
}
