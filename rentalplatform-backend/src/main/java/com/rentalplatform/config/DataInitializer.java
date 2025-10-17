package com.rentalplatform.config;

import com.rentalplatform.model.Role;
import com.rentalplatform.model.RoleName;
import com.rentalplatform.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            adminRole.setDescription("Administrator role with full access");
            roleRepository.save(adminRole);

            Role ownerRole = new Role();
            ownerRole.setName(RoleName.ROLE_OWNER);
            ownerRole.setDescription("Equipment owner role");
            roleRepository.save(ownerRole);

            Role clientRole = new Role();
            clientRole.setName(RoleName.ROLE_CLIENT);
            clientRole.setDescription("Client role for renting equipment");
            roleRepository.save(clientRole);

            System.out.println("Default roles created successfully!");
        }
    }
}