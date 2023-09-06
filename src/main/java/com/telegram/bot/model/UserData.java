package com.telegram.bot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_dates", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @JoinColumn(name = "name_surname")
    private String nameSurname;

    private String phone;

    private String email;

    @JoinColumn(name = "appointment_number", unique = true)
    private String appointmentNumber;

    private City city;

    @JoinColumn(name = "created_at")
    private ZonedDateTime createdAt;

    @JoinColumn(name = "is_registered")
    private boolean isRegistered;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Vladivostok"));
        }
    }

    public String getLogInfo() {
        return String.format("%s %s %s %s", getNameSurname(), getPhone(), getEmail(), getAppointmentNumber());
    }
}
