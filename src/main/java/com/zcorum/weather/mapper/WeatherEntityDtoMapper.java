package com.zcorum.weather.mapper;


import com.zcorum.weather.dto.WeatherDto;
import com.zcorum.weather.entity.WeatherEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WeatherEntityDtoMapper {
	WeatherEntity dtoToEntity(WeatherDto dto);

	WeatherDto entityToDto(WeatherEntity entity);

	List<WeatherDto> entitiesToDtos(List<WeatherEntity> entities);
}
