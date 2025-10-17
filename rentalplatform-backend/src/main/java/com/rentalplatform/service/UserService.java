package com.rentalplatform.service;

import com.rentalplatform.dto.CreateUserRequest;
import com.rentalplatform.dto.UpdateUserRequest;
import com.rentalplatform.dto.UserDTO;
import com.rentalplatform.model.Role;
import com.rentalplatform.model.RoleName;
import com.rentalplatform.model.User;
import com.rentalplatform.repository.RoleRepository;
import com.rentalplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get user by ID
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    // Get user by email
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return convertToDTO(user);
    }

    // Create new user
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Set roles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            request.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            });
        } else {
            // Default role is CLIENT
            Role clientRole = roleRepository.findByName(RoleName.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(clientRole);
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // Update user
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check if email is being changed and if it's already in use
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(request.getEmail());
        }

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            request.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            });
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Delete user
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // Deactivate user (soft delete)
    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsActive(false);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Activate user
    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsActive(true);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Get users by role
    public List<UserDTO> getUsersByRole(String roleName) {
        RoleName role = RoleName.valueOf(roleName);
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(r -> r.getName().equals(role)))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get active users
    public List<UserDTO> getActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::getIsActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get inactive users
    public List<UserDTO> getInactiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getIsActive())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert User entity to UserDTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        return dto;
    }
}