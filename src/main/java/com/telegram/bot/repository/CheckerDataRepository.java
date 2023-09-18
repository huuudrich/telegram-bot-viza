package com.telegram.bot.repository;

import com.telegram.bot.model.data.CheckerData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckerDataRepository extends JpaRepository<CheckerData, Long> {
}
