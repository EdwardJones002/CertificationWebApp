package com.user.certifications.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "requested_certifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestedCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String examBoard;
    private String skills;
    private String learningTime;
    @Column(length = 2000)
    private String about;
    private String hyperlink;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
