package com.user.certifications.tests;
import com.user.certifications.entities.User;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.UserCertificationRepository;
import com.user.certifications.repositories.UserRepository;
import com.user.certifications.servicesImp.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.internal.matchers.text.ValuePrinter.print;

@SpringBootTest
public class user_tests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCertificationRepository userCertificationRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;
    @Test
    void createUser() {
        //Create new User
        User user = new User();
        user.setName("TestUser");
        user.setUsername("TestUser@gmail.com");
        user.setPassword("abcd1234");
        user.setRole("USER");
        //Save user to repository
        userRepository.save(user);
        //Check user is there
        assertTrue(userRepository.findByUsername("TestUser@gmail.com").isPresent());
        userRepository.delete(user);
    }

    @Test
    void createAdminUser() {
        //Create new User
        User user = new User();
        user.setName("TestUser1");
        user.setUsername("TestUser1@gmail.com");
        user.setPassword("abcd1234");
        user.setRole("ADMIN");
        //Save user to repository
        userRepository.save(user);
        //Check user is there and has admin role
        assertTrue(userRepository.findByUsername("TestUser1@gmail.com").isPresent());
        assertEquals("ADMIN", user.getRole());
        //delete user
        userRepository.delete(user);
        //Check user has been deleted
        assertTrue(userRepository.findByUsername("TestUser1@gmail.com").isEmpty());
    }

    @Test
    void findNonUserByUsername() {
        //Test for non-existent username
        var nonExistentUser = userRepository.findByUsername("nonexistent@gmail.com");
        assertTrue(nonExistentUser.isEmpty());
    }

    @Test
    void testUserDuplication(){
        //Create new User
        User user = new User();
        user.setName("TestUser2");
        user.setUsername("TestUser2@gmail.com");
        user.setPassword("abcd1234");
        user.setRole("USER");
        userRepository.save(user);

        User user1 = new User();
        user1.setName("TestUser2");
        user1.setUsername("TestUser2@gmail.com");
        user1.setPassword("abcd1234");
        user1.setRole("USER");

        try{
            userRepository.save(user1);
        }catch(Exception e) {
            print(e.getMessage());
        } finally {
            userRepository.delete(user);
        }
    }

    @Test
    void passwordEncodingTest() {
        //Create User
        User user = new User();
        user.setName("SecureUser");
        user.setUsername("secureuser@gmail.com");
        user.setPassword(passwordEncoder.encode("plaintextpassword"));
        user.setRole("USER");
        //Save User
        userRepository.save(user);
        var foundUser = userRepository.findByUsername("secureuser@gmail.com");
        assertTrue(foundUser.isPresent());
        //Ensure Password is encoded
        assertTrue(passwordEncoder.matches("plaintextpassword", foundUser.get().getPassword()));
        userRepository.delete(user);
    }
    @Test
    void testUpdateUsername(){
        //New User Creds
        String newName = "ted";
        String newPass = "Edward123";
        String newEmail = "Edward@gmail.com";
        //Create User
        User user = new User();
        user.setName("UpdateUser");
        user.setUsername("updateUser@gmail.com");
        user.setPassword(passwordEncoder.encode("plaintextpassword"));
        user.setRole("USER");
        userRepository.save(user);
        assertTrue(userRepository.findByUsername("updateUser@gmail.com").isPresent());
        userService.updateUserProfile(user, newName, newEmail, newPass);
        //Ensure that updated user details are present in db
        assertTrue(userRepository.findByUsername(newEmail).isPresent());
        //Delete User
        user.setUsername(newEmail);
        user.setPassword(passwordEncoder.encode("Edward123"));
        user.setName("Edward");
        userRepository.delete(user);
    }

    @Test
    void testUserLogin(){
        //Create User
        User user = new User();
        user.setName("UserLogin");
        user.setUsername("UserLogin@gmail.com");
        user.setPassword(passwordEncoder.encode("userLogin"));
        user.setRole("USER");
        userRepository.save(user);

        //Delete User
        userRepository.delete(user);
    }

}
