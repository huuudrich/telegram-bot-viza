package com.telegram.bot.repository;

import com.telegram.bot.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserData, Long> {
}