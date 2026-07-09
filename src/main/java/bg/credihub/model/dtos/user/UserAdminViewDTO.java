package bg.credihub.model.dtos.user;

import bg.credihub.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminViewDTO {
    private UUID id;
    private String fullName;
    private String email;
    private Role role;
}
