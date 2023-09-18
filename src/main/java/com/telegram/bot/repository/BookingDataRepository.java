package com.telegram.bot.repository;

import com.telegram.bot.model.data.BookingData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDataRepository extends JpaRepository<BookingData, Long> {
}