package com.user.certifications.repositories;

import com.user.certifications.entities.Certification;
import com.user.certifications.entities.User;
import com.user.certifications.entities.UserCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {
    List<UserCertification> findByUserId(Long userId);

    boolean existsByUserAndCertification(User user, Certification certification);
}
