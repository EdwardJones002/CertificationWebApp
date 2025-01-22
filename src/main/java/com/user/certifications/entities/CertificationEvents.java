package com.user.certifications.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Entity
@Table(name = "CertificationEvents")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificationEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String certificationName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hyperlink;
}
