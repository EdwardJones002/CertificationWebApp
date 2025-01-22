package com.user.certifications.repositories;

import com.user.certifications.entities.CertificationEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface CertificationEventRepository extends JpaRepository<CertificationEvents, Long> {
    List<CertificationEvents> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
