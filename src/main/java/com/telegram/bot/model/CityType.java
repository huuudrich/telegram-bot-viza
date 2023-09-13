package com.telegram.bot.model;

import java.util.List;
import java.util.stream.Collectors;

public enum CityType {
    MSK("Москва"),
    SPB("Санкт-Петербург"),
    IRK("Иркутск"),
    EKB("Екатериебург"),
    VDK("Владивосток"),
    KHV("Хабаровск");

    private final String name;

    CityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CityType fromCode(String code) {
        for (CityType cityType : CityType.values()) {
            if (cityType.name().equals(code)) {
                return cityType;
            }
        }
        throw new IllegalArgumentException("No city found for code: " + code);
    }

    public static CityType toCode(String name) {
        for (CityType cityType : CityType.values()) {
            if (cityType.getName().equals(name)) {
                return cityType;
            }
        }
        throw new IllegalArgumentException("No city found for city name: " + name);
    }
    public static List<String> convertCodesToNames(List<String> cityCodes) {
        return cityCodes.stream()
                .map(CityType::valueOf)
                .map(CityType::getName)
                .collect(Collectors.toList());
    }
}
