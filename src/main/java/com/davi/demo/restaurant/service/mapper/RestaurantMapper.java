package com.davi.demo.restaurant.service.mapper;

import com.davi.demo.restaurant.service.dto.RestaurantResponseDTO;
import com.davi.demo.restaurant.service.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RestaurantMapper  {
    RestaurantResponseDTO toRestaurantResponseDTO(Restaurant entity);
}
