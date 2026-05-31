package cz.cvut.fel.nss.accommodation.dto;

import cz.cvut.fel.nss.accommodation.entity.MealPlan;

public record AccommodationDto(Long id,
                               String name,
                               String city,
                               int capacity,
                               int stars,
                               MealPlan mealPlan,
                               String address,
                               double pricePerNight,
                               String roomType
) {}
