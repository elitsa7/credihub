package bg.credihub;

import bg.credihub.exception.*;
import bg.credihub.mapper.UserMapper;
import bg.credihub.model.dtos.user.UpdateUserProfileRequest;
import bg.credihub.model.dtos.user.UserAdminViewDTO;
import bg.credihub.model.dtos.user.UserProfileDTO;
import bg.credihub.model.dtos.user.UserRegisterDTO;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.UserRepository;
import bg.credihub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                modelMapper,
                passwordEncoder,
                userMapper);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        User user = createUser();
        UserRegisterDTO userRegisterDTO = createUserRegisterDTO();

        when(userRepository.existsByEmail(userRegisterDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())).thenReturn(false);
        when(userRepository.existsByIdentificationNumber(userRegisterDTO.getIdentificationNumber())).thenReturn(false);
        when(modelMapper.map(userRegisterDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(userRegisterDTO.getPassword())).thenReturn("encodedPassword");

        userService.register(userRegisterDTO);

        assertEquals(Role.USER, user.getRole());
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository).save(user);
        verify(modelMapper).map(userRegisterDTO, User.class);
        verify(passwordEncoder).encode(userRegisterDTO.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRegisterDTO userRegisterDTO = createUserRegisterDTO();

        when(userRepository.existsByEmail(userRegisterDTO.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.register(userRegisterDTO));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verify(modelMapper, never()).map(any(), eq(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringPhoneNumberAlreadyExists() {
        UserRegisterDTO userRegisterDTO = createUserRegisterDTO();

        when(userRepository.existsByEmail(userRegisterDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())).thenReturn(true);

        assertThrows(PhoneNumberAlreadyExistsException.class,
                () -> userService.register(userRegisterDTO));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verify(modelMapper, never()).map(any(), eq(User.class));
    }

    @Test
    void shouldThrowExceptionWhenIdentificationNumberAlreadyExists() {
        UserRegisterDTO userRegisterDTO = createUserRegisterDTO();

        when(userRepository.existsByEmail(userRegisterDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())).thenReturn(false);
        when(userRepository.existsByIdentificationNumber(userRegisterDTO.getIdentificationNumber())).thenReturn(true);

        assertThrows(IdentificationNumberAlreadyExistsException.class,
                () -> userService.register(userRegisterDTO));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verify(modelMapper, never()).map(any(), eq(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoNotMatch() {
        UserRegisterDTO userRegisterDTO = createUserRegisterDTO();
        userRegisterDTO.setConfirmPassword("DifferentPassword123!");

        when(userRepository.existsByEmail(userRegisterDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())).thenReturn(false);
        when(userRepository.existsByIdentificationNumber(userRegisterDTO.getIdentificationNumber())).thenReturn(false);

        assertThrows(PasswordsDoNotMatchException.class,
                () -> userService.register(userRegisterDTO));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verify(modelMapper, never()).map(any(), eq(User.class));
    }

    @Test
    void shouldMakeModeratorSuccessfully() {
        User user = createUser();
        user.setRole(Role.USER);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.makeModerator(user.getId());

        assertEquals(Role.MODERATOR, user.getRole());
        verify(userRepository).save(user);
        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenMakingAdminModerator() {
        User user = createUser();
        user.setRole(Role.ADMIN);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(RoleModificationException.class,
                () -> userService.makeModerator(user.getId()));

        verify(userRepository, never()).save(any());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenMakingModeratorForNonExistingUser() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.makeModerator(id));

        verify(userRepository, never()).save(any());
        verify(userRepository).findById(id);

    }

    @Test
    void shouldRemoveModeratorSuccessfully() {
        User user = createUser();
        user.setRole(Role.MODERATOR);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.removeModerator(user.getId());

        assertEquals(Role.USER, user.getRole());
        verify(userRepository).save(user);
        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenRemovingAdminModerator() {
        User user = createUser();
        user.setRole(Role.ADMIN);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(RoleModificationException.class,
                () -> userService.removeModerator(user.getId()));

        verify(userRepository, never()).save(any());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenRemovingModeratorForNonExistingUser() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.removeModerator(id));

        verify(userRepository, never()).save(any());
        verify(userRepository).findById(id);
    }

    @Test
    void shouldReturnUserByIdSuccessfully() {
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getById(user.getId());

        assertEquals(user, result);
        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(id));

        verify(userRepository).findById(id);
    }

    @Test
    void shouldReturnUserProfileWithMaskedIdentificationNumber() {
        User user = createUser();
        UserProfileDTO userProfileDTO = createUserProfileDTO();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.getProfile(user.getId());

        assertEquals("******7890", result.getMaskedIdentificationNumber());
        verify(userRepository).findById(user.getId());
        verify(modelMapper).map(user, UserProfileDTO.class);
    }

    @Test
    void shouldReturnUserProfileWhenIdentificationNumberIsNull() {
        User user = createUser();
        user.setIdentificationNumber(null);
        UserProfileDTO userProfileDTO = createUserProfileDTO();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.getProfile(user.getId());

        assertNull(result.getMaskedIdentificationNumber());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenGettingUserProfileForNonExistingUser() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getProfile(id));

        verify(modelMapper, never()).map(any(), eq(UserProfileDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenIdentificationNumberIsShorterThanFourDigits(){
        User user = createUser();
        user.setIdentificationNumber("123");
        UserProfileDTO userProfileDTO = createUserProfileDTO();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDTO.class)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.getProfile(user.getId());

        assertNull(result.getMaskedIdentificationNumber());
        verify(userRepository).findById(user.getId());
        verify(modelMapper).map(user, UserProfileDTO.class);
    }

    @Test
    void shouldReturnUserProfileForEditSuccessfully() {
        User user = createUser();
        UpdateUserProfileRequest updateUserProfileRequest = createUpdateUserProfileRequest();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UpdateUserProfileRequest.class)).thenReturn(updateUserProfileRequest);

        UpdateUserProfileRequest result = userService.getProfileForEdit(user.getId());

        assertEquals(updateUserProfileRequest, result);
        verify(modelMapper).map(user, UpdateUserProfileRequest.class);
    }

    @Test
    void shouldThrowExceptionWhenGettingProfileForEditForNonExistingUser() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getProfileForEdit(id));
        verify(modelMapper, never()).map(any(), eq(UpdateUserProfileRequest.class));
    }

    @Test
    void shouldReturnAllUsersWithoutAdmin(){
        User admin = createUser();
        admin.setRole(Role.ADMIN);

        User user = createUser();
        user.setRole(Role.USER);

        UserAdminViewDTO userAdminViewDTO = new UserAdminViewDTO();

        when(userRepository.findAll()).thenReturn(List.of(admin,user));
        when(userMapper.toAdminViewDto(user)).thenReturn(userAdminViewDTO);

        List<UserAdminViewDTO> result = userService.getAllWithoutAdminView();
        assertEquals(1, result.size());
        verify(userMapper).toAdminViewDto(user);
    }

    @Test
    void shouldReturnEmptyUserList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserAdminViewDTO> result = userService.getAllWithoutAdminView();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void shouldUpdateProfileSuccessfully() {
        User user = createUser();
        UpdateUserProfileRequest updateUserProfileRequest = createUpdateUserProfileRequest();
        updateUserProfileRequest.setFirstName("Peter");
        updateUserProfileRequest.setLastName("Petrov");
        updateUserProfileRequest.setPhoneNumber("0899999999");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByPhoneNumber(updateUserProfileRequest.getPhoneNumber())).thenReturn(Optional.empty());

        userService.updateProfile(user.getId(), updateUserProfileRequest);

        assertEquals("Peter", user.getFirstName());
        assertEquals("Petrov", user.getLastName());
        assertEquals("0899999999", user.getPhoneNumber());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPhoneNumberAlreadyExists() {
        User user = createUser();
        User anotherUser = createUser();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setPhoneNumber("0899999999");
        UpdateUserProfileRequest updateUserProfileRequest = createUpdateUserProfileRequest();
        updateUserProfileRequest.setPhoneNumber("0899999999");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(userRepository.findByPhoneNumber(updateUserProfileRequest.getPhoneNumber())).thenReturn(Optional.of(anotherUser));

        assertThrows(PhoneNumberAlreadyExistsException.class,
                () -> userService.updateProfile(user.getId(), updateUserProfileRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        UUID id = UUID.randomUUID();

        UpdateUserProfileRequest updateUserProfileRequest = createUpdateUserProfileRequest();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(id, updateUserProfileRequest));
        verify(userRepository, never()).save(any());
    }

    private User createUser() {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setEmail("ivan@test.com");
        user.setPassword("encodedPassword");
        user.setPhoneNumber("0888123456");
        user.setIdentificationNumber("1234567890");
        user.setRole(Role.USER);

        return user;
    }

    private UserRegisterDTO createUserRegisterDTO() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();

        userRegisterDTO.setFirstName("Ivan");
        userRegisterDTO.setLastName("Ivanov");
        userRegisterDTO.setPhoneNumber("0888123456");
        userRegisterDTO.setIdentificationNumber("1234567890");
        userRegisterDTO.setEmail("ivan@test.com");
        userRegisterDTO.setPassword("Password123!");
        userRegisterDTO.setConfirmPassword("Password123!");

        return userRegisterDTO;
    }

    private UserProfileDTO createUserProfileDTO() {
        UserProfileDTO userProfileDTO = new UserProfileDTO();

        userProfileDTO.setFirstName("Ivan");
        userProfileDTO.setLastName("Ivanov");
        userProfileDTO.setPhoneNumber("0888123456");
        userProfileDTO.setEmail("ivan@test.com");
        userProfileDTO.setIdentificationNumber("1234567890");
        userProfileDTO.setRole(Role.USER);

        return userProfileDTO;
    }

    private UpdateUserProfileRequest createUpdateUserProfileRequest() {
        UpdateUserProfileRequest updateUserProfileRequest = new UpdateUserProfileRequest();

        updateUserProfileRequest.setFirstName("Ivan");
        updateUserProfileRequest.setLastName("Ivanov");
        updateUserProfileRequest.setPhoneNumber("0888123456");

        return updateUserProfileRequest;
    }
}
