package com.telegram.bot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "proxies", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChinaProxy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(name = "ip_address")
    private String ipAddress;

    private int port;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    private Boolean valid;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Vladivostok"));
        }
        if (this.valid == null) {
            this.valid = true;
        }
    }

    @Override
    public String toString() {
        return "%s. %s:%s:%s:%s".formatted(id, username, password, ipAddress, port);
    }
}
