package com.user.certifications.repositories;

import com.user.certifications.entities.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findTop5ByOrderByDateAddedDesc();

    List<Certification> findAllByOrderByExamBoardAscDateAddedDesc();

    @Query("SELECT DISTINCT c.skills FROM Certification c")
    List<String> findDistinctSkills();

    List<Certification> findBySkillsOrderByDateAddedDesc(String randomSkill);

    List<Certification> findByNameContainingIgnoreCaseOrSkillsContainingIgnoreCase(String query, String query1);

    boolean existsByHyperlink(String hyperlink);
}
