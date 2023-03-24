package com.zcorum.weather.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.zcorum.weather.constants.Constants.yyyy_MM_dd;

/*
	## Data:
	Example of a weather data JSON object:
	```
	{
		"id": 1,
		"date": "1985-01-01",
		"lat": 36.1189,
		"lon": -86.6892,
		"city": "Nashville",
		"state": "Tennessee",
		"temperatures": [17.3, 16.8, 16.4, 16.0, 15.6, 15.3, 15.0, 14.9, 15.8, 18.0, 20.2, 22.3, 23.8, 24.9, 25.5, 25.7, 24.9, 23.0, 21.7, 20.8, 29.9, 29.2, 28.6, 28.1]
	}
	```
 */
@Data
public class WeatherDto {

	private Integer id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = yyyy_MM_dd)
	private Date date;
	private Double lat;
	private Double lon;
	private String city;
	private String state;
	private List<Double> temperatures;

	@Override
	public String toString() {
		return "WeatherDto{" +
				"id=" + id +
				", date=" + Optional.ofNullable(date).map(d -> new SimpleDateFormat(yyyy_MM_dd).format(d)).orElse(null) +
				", lat=" + lat +
				", lon=" + lon +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", temperatures=" + temperatures +
				'}';
	}
}
