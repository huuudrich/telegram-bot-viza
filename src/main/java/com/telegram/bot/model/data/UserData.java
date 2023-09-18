package com.telegram.bot.model.data;

import com.telegram.bot.model.CityType;
import com.telegram.bot.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    private String passport;

    @JoinColumn(name = "appointment_number", unique = true)
    private String appointmentNumber;

    @JoinColumn(name = "city_type")
    private CityType cityType;

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

    @Override
    public String toString() {
        return "UserData{" +
                "id=" + id +
                ", owner=" + owner +
                ", nameSurname='" + nameSurname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", passport='" + passport + '\'' +
                ", appointmentNumber='" + appointmentNumber + '\'' +
                ", cityType=" + cityType +
                ", createdAt=" + createdAt +
                ", isRegistered=" + isRegistered +
                '}';
    }
}
