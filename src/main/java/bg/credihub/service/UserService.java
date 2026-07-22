package bg.credihub.service;

import bg.credihub.exception.*;
import bg.credihub.mapper.UserMapper;
import bg.credihub.model.dtos.user.UpdateUserProfileRequest;
import bg.credihub.model.dtos.user.UserAdminViewDTO;
import bg.credihub.model.dtos.user.UserProfileDTO;
import bg.credihub.model.dtos.user.UserRegisterDTO;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public void register(UserRegisterDTO userRegisterDTO) {
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered.");
        }

        if (userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number is already registered.");
        }

        if (userRepository.existsByIdentificationNumber(userRegisterDTO.getIdentificationNumber())) {
            throw new IdentificationNumberAlreadyExistsException("Identification number is already registered.");
        }

        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match.");
        }

        User user = modelMapper.map(userRegisterDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        log.info("New user registered with email {}", user.getEmail());
    }

    public void makeModerator(UUID userId) {
        User user = getById(userId);

        if (user.getRole().equals(Role.ADMIN)) {
            log.warn("Attempt to change ADMIN role for user {}", userId);
            throw new RoleModificationException("Admin role cannot be changed.");
        }

        user.setRole(Role.MODERATOR);
        userRepository.save(user);
        log.info("User {} promoted to moderator", userId);
    }

    public void removeModerator(UUID userId) {
        User user = getById(userId);

        if (user.getRole().equals(Role.ADMIN)) {
            log.warn("Attempt to change ADMIN role for user {}", userId);
            throw new RoleModificationException("Admin role cannot be changed.");
        }

        user.setRole(Role.USER);
        userRepository.save(user);
        log.info("Moderator {} demoted to user", userId);
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    public List<UserAdminViewDTO> getAllWithoutAdminView() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() != Role.ADMIN)
                .map(userMapper::toAdminViewDto)
                .toList();
    }

    public UserProfileDTO getProfile(UUID id) {
        User user = getById(id);

        UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);

        String idNumber = user.getIdentificationNumber();

        if (idNumber != null && idNumber.length() >= 4) {
            userProfileDTO.setMaskedIdentificationNumber("*".repeat(idNumber.length() - 4)
                    + idNumber.substring(idNumber.length() - 4));
        }

        return userProfileDTO;
    }

    public UpdateUserProfileRequest getProfileForEdit(UUID id) {
        User user = getById(id);
        return modelMapper.map(user, UpdateUserProfileRequest.class);
    }

    public void updateProfile(UUID userId, UpdateUserProfileRequest userProfileRequest) {
        User user = getById(userId);

        userRepository.findByPhoneNumber(userProfileRequest.getPhoneNumber())
                .filter(exists -> !exists.getId().equals(userId))
                .ifPresent(exists -> {
                    throw new PhoneNumberAlreadyExistsException("Phone number already exists.");
                });

        user.setFirstName(userProfileRequest.getFirstName());
        user.setLastName(userProfileRequest.getLastName());
        user.setPhoneNumber(userProfileRequest.getPhoneNumber());

        userRepository.save(user);
        log.info("User {} updated profile", userId);
    }
}
