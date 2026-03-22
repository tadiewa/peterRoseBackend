//package com.peterrose.peterrose.config;
//
//import com.peterrose.peterrose.model.User;
//import com.peterrose.peterrose.constants.UserRole;
//import com.peterrose.peterrose.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
///**
// * DataLoader to create initial admin user
// * Runs on application startup
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class AdminDataLoader implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) {
//        createInitialAdminUser();
//    }
//
//    private void createInitialAdminUser() {
//        // Check if admin already exists
//        if (userRepository.findByEmail("admin@peterrose.com").isPresent()) {
//            log.info("Admin user already exists");
//            return;
//        }
//
//        // Create admin user
//        User admin = new User();
//        admin.setEmail("admin@peterrose.com");
//        admin.setPassword(passwordEncoder.encode("Admin@123"));  // Change this password!
//        admin.setFirstName("Admin");
//        admin.setLastName("User");
//        admin.setRole(UserRole.ADMIN);
//        admin.setActive(true);
//
//        userRepository.save(admin);
//
//        log.info(" Initial admin user created:");
//        log.info("   Email: admin@peterrose.com");
//        log.info("   Password: okay
//        ");
//        log.info("   ⚠  CHANGE THIS PASSWORD IN PRODUCTION!");
//    }
//}