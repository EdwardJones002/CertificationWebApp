package com.user.certifications.repositories;

import com.user.certifications.entities.RequestedCertification;
import com.user.certifications.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestedCertificationRepository extends JpaRepository<RequestedCertification, Long> {

    List<RequestedCertification> findByUser(User user);

    void deleteAllByHyperlink(String hyperlink);

    List<RequestedCertification> findAllByHyperlink(String hyperlink);

    boolean existsByUserAndHyperlink(User user, String hyperlink);
}
