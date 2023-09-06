package com.telegram.bot.model;

import java.util.List;
import java.util.stream.Collectors;

public enum City {
    MSK("Москва"),
    SPB("Санкт-Петербург"),
    IRK("Иркутск"),
    EKB("Екатериебург"),
    VDK("Владивосток"),
    KHV("Хабаровск");

    private final String name;

    City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static City fromCode(String code) {
        for (City city : City.values()) {
            if (city.name().equals(code)) {
                return city;
            }
        }
        throw new IllegalArgumentException("No city found for code: " + code);
    }

    public static List<String> convertCodesToNames(List<String> cityCodes) {
        return cityCodes.stream()
                .map(City::valueOf)
                .map(City::getName)
                .collect(Collectors.toList());
    }
}
