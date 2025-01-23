package org.myteam.server.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "p_refreshes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refresh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    private String email;
    @Column(name = "public_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID publicId;
    @Column(nullable = false, columnDefinition="BLOB")
    private String refresh;
    @Column(nullable = false)
    private String expiration;

    @Builder
    public Refresh(UUID publicId, String refresh, String expiration) {
        this.publicId = publicId;
        this.refresh = refresh;
        this.expiration = expiration;
    }
}