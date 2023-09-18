package com.telegram.bot.model.data;

import com.telegram.bot.model.CityType;
import com.telegram.bot.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "checkers_data", schema = "public")
@Getter
@Setter
public class CheckerData extends UserData {
    public CheckerData(Long id, User owner, String nameSurname, String phone, String email, String passport, String appointmentNumber, CityType cityType, ZonedDateTime createdAt, boolean isRegistered) {
        super(id, owner, nameSurname, phone, email, passport, appointmentNumber, cityType, createdAt, isRegistered);
    }

    public CheckerData() {
    }
}
