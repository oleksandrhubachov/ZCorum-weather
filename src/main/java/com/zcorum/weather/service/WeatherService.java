package com.zcorum.weather.service;


import com.zcorum.weather.dto.WeatherDto;

import java.util.List;

public interface WeatherService {


	WeatherDto create(WeatherDto request);

	WeatherDto findById(Integer id);

	List<WeatherDto> search(String date, String city, String sort);
}
