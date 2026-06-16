package bg.credihub.service;

import bg.credihub.exception.*;
import bg.credihub.model.dtos.UserLoginDTO;
import bg.credihub.model.dtos.UserRegisterDTO;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
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
        Role role = userRepository.count() == 0 ? Role.ADMIN : Role.USER;
        user.setRole(role);

        userRepository.save(user);
    }

    public User login(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new InvalidLoginException("Invalid email or password."));

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new InvalidLoginException("Invalid email or password.");
        }

        return user;
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
