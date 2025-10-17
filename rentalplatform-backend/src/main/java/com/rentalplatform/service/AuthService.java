package com.rentalplatform.service;

import com.rentalplatform.dto.JwtResponse;
import com.rentalplatform.dto.LoginRequest;
import com.rentalplatform.dto.SignUpRequest;
import com.rentalplatform.model.Role;
import com.rentalplatform.model.RoleName;
import com.rentalplatform.model.User;
import com.rentalplatform.repository.RoleRepository;
import com.rentalplatform.repository.UserRepository;
import com.rentalplatform.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtResponse login(LoginRequest loginRequest) {
        try {
            // Authenticate - this will throw exception if credentials are wrong
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate JWT token
            String jwt = jwtService.generateToken(userDetails);

            // Get user from database
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return new JwtResponse(jwt, user.getId(), user.getEmail(), user.getFullName(), roles);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        } catch (DisabledException e) {
            throw new DisabledException("Votre compte est désactivé. Veuillez contacter l'administrateur");
        }
    }

    public String register(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Un compte existe déjà avec cet email");
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFullName(signUpRequest.getFullName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setIsActive(true);

        Set<Role> roles = new HashSet<>();
        if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
            signUpRequest.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            });
        } else {
            Role clientRole = roleRepository.findByName(RoleName.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(clientRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return "Utilisateur enregistré avec succès!";
    }
}