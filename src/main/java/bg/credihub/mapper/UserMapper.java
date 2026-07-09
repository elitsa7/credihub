package bg.credihub.mapper;

import bg.credihub.model.dtos.user.UserAdminViewDTO;
import bg.credihub.model.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserAdminViewDTO toAdminViewDto(User user) {
        return new UserAdminViewDTO(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
