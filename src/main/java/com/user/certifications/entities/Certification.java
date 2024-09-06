package com.user.certifications.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String examBoard;
    private String skills;
    private String learningTime;
    @Column(length = 2000)
    private String about;
    private String hyperlink;
    private Date dateAdded;
}
