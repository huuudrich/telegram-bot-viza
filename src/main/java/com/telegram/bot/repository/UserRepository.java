package com.telegram.bot.repository;

import com.telegram.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByTelegramId(Long telegramId);
}
