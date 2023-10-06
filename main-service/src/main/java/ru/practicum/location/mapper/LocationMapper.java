package ru.practicum.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

@UtilityClass
public class LocationMapper {
    public LocationDto locationToDto(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location не может быть null");
        }
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public Location dtoToLocation(LocationDto locationDto) {
        if (locationDto == null) {
            throw new IllegalArgumentException("LocationDto не может быть null");
        }
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
