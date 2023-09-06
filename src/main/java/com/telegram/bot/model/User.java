package com.telegram.bot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "issue_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    //дата регистрации
    private LocalDateTime issueDate;

    @Column(name = "orders_limit")
    private Integer ordersLimit;

    @Column(name = "orders_counter")
    private Integer ordersCounter;

    @Column(name = "is_active")
    private boolean isActive;
}
