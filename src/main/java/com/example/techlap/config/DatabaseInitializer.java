package com.example.techlap.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.Permission;
import com.example.techlap.domain.Role;
import com.example.techlap.domain.User;
import com.example.techlap.repository.CustomerRepository;
import com.example.techlap.repository.PermissionRepository;
import com.example.techlap.repository.RoleRepository;
import com.example.techlap.repository.UserRepository;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();
        long countCustomers = this.customerRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));
            arr.add(new Permission("Filter Permissions", "/api/v1/permissions/filter", "POST", "PERMISSIONS"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));
            arr.add(new Permission("Filter Roles", "/api/v1/roles/filter", "POST", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));
            arr.add(new Permission("Filter Users", "/api/v1/users/filter", "POST", "USERS"));
            arr.add(new Permission("Get dashboard", "/api/v1/user/dashboard", "GET", "USERS"));

            arr.add(new Permission("Create a customer", "/api/v1/customers", "POST", "CUSTOMERS"));
            arr.add(new Permission("Update a customer", "/api/v1/customers", "PUT", "CUSTOMERS"));
            arr.add(new Permission("Delete a customer", "/api/v1/customers/{id}", "DELETE", "CUSTOMERS"));
            arr.add(new Permission("Get a customer by id", "/api/v1/customers/{id}", "GET", "CUSTOMERS"));
            arr.add(new Permission("Get customers with pagination", "/api/v1/customers", "GET", "CUSTOMERS"));
            arr.add(new Permission("Filter Customers", "/api/v1/customers/filter", "POST", "CUSTOMERS"));

            arr.add(new Permission("Create a brand", "/api/v1/brands", "POST", "BRANDS"));
            arr.add(new Permission("Update a brand", "/api/v1/brands", "PUT", "BRANDS"));
            arr.add(new Permission("Delete a brand", "/api/v1/brands/{id}", "DELETE", "BRANDS"));
            arr.add(new Permission("Get a brand by id", "/api/v1/brands/{id}", "GET", "BRANDS"));
            arr.add(new Permission("Get brands with pagination", "/api/v1/brands", "GET", "BRANDS"));
            arr.add(new Permission("Filter Brands", "/api/v1/brands/filter", "POST", "BRANDS"));

            arr.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES"));
            arr.add(new Permission("Update a category", "/api/v1/categories", "PUT", "CATEGORIES"));
            arr.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES"));
            arr.add(new Permission("Get a category by id", "/api/v1/categories/{id}", "GET", "CATEGORIES"));
            arr.add(new Permission("Get categories with pagination", "/api/v1/categories", "GET", "CATEGORIES"));
            arr.add(new Permission("Filter Categories", "/api/v1/categories/filter", "POST", "CATEGORIES"));

            arr.add(new Permission("Create a product", "/api/v1/products", "POST", "PRODUCTS"));
            arr.add(new Permission("Update a product", "/api/v1/products", "PUT", "PRODUCTS"));
            arr.add(new Permission("Delete a product", "/api/v1/products/{id}", "DELETE", "PRODUCTS"));
            arr.add(new Permission("Get a product by id", "/api/v1/products/{id}", "GET", "PRODUCTS"));
            arr.add(new Permission("Get products with pagination", "/api/v1/products", "GET", "PRODUCTS"));
            arr.add(new Permission("Filter Products", "/api/v1/products/filter", "POST", "PRODUCTS"));

            arr.add(new Permission("Create a order", "/api/v1/orders", "POST", "ORDERS"));
            arr.add(new Permission("Update a order", "/api/v1/orders", "PUT", "ORDERS"));
            arr.add(new Permission("Delete a order", "/api/v1/orders/{id}", "DELETE", "ORDERS"));
            arr.add(new Permission("Get a order by id", "/api/v1/orders/{id}", "GET", "ORDERS"));
            arr.add(new Permission("Get orders with pagination", "/api/v1/orders", "GET", "ORDERS"));
            arr.add(new Permission("Filter Orders", "/api/v1/orders/filter", "POST", "ORDERS"));

            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("thiendz@gmail.com");
            adminUser.setAddress("Hồ Chí Minh");
            adminUser.setFullName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("210704Thien@"));
            adminUser.setPhone("0382143560");

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}