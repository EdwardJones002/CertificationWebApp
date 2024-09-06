package com.user.certifications.repositories;

import com.user.certifications.entities.Certification;
import com.user.certifications.entities.User;
import com.user.certifications.entities.UserFlaggedCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFlaggedCertificationRepository extends JpaRepository<UserFlaggedCertification, Long> {

    List<UserFlaggedCertification> findByUserId(Long userId);

    void deleteAllByUserAndCertification(User user, Certification certification);

    boolean existsByUserAndCertification(User user1, Certification certification);
}
