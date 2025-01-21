package com.user.certifications.config;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.user.certifications.entities.Certification;
import com.user.certifications.entities.User;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.List;


@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() throws FileNotFoundException {
        createCertifications();
        if (userRepository.findByUsername("admin@gmail.com").isEmpty()) {
            User user = new User();
            user.setName("Admin");
            user.setUsername("admin@gmail.com");
            user.setPassword(passwordEncoder.encode("abcd1234"));
            user.setRole("ADMIN");
            userRepository.save(user);
            System.out.println("Initialized user Admin with email admin@gmail.com and encoded password.");
        } else {
            System.out.println("User with email admin@gmail.com already exists.");
        }
    }
    private void createCertifications() throws FileNotFoundException {
        String filePath="src/main/java/com/user/certifications/config/certifications.csv";
        try(CSVReader reader = new CSVReader(new FileReader(filePath))){
            List<String[]> records = reader.readAll();
            for (int i = 1; i < records.size();i++){
                String[] record = records.get(i);
                if (!certificationRepository.existsByHyperlink(record[2])){
                    Certification cert = new Certification();
                    cert.setName(record[0]);
                    cert.setAbout(record[1]);
                    cert.setHyperlink(record[2]);
                    cert.setSkills(record[3]);
                    cert.setExamBoard(record[4]);
                    cert.setLearningTime(record[5]);
                    cert.setDateAdded(new Date());
                    certificationRepository.save(cert);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
