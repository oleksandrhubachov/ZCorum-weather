package com.zcorum.weather.web.controller;

import com.zcorum.weather.dto.WeatherDto;
import com.zcorum.weather.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

	private final WeatherService weatherService;

	public WeatherController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	/*
	POST request to `/weather`:
	- creates a new weather data record
	- expects a valid weather data object as its body payload, except that it does not have an id property; you can assume that the given object is always valid
	- adds the given object to the collection and assigns a unique integer id to it
	- the response code is 201 and the response body is the created record, including its unique id
	 */

	@PostMapping
	public ResponseEntity<?> create(@RequestBody WeatherDto request) {
		WeatherDto createdRecord = weatherService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
	}

	/*
	GET request to `/weather/<id>`:
	- returns a record with the given id
	- if the matching record exists, the response code is 200 and the response body is the matching object
	- if there is no record in the collection with the given id, the response code is 404
	 */

	@GetMapping("/{id}")
	public ResponseEntity<?> findOneById(@PathVariable("id") Integer id) {
		WeatherDto record = weatherService.findById(id);
		if (record == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(record);
	}

	/*
	GET request to `/weather`:
	- the response code is 200
	- the response body is an array of matching records, ordered by their ids in increasing order
	- accepts an optional query string parameter, date, in the format YYYY-MM-DD, for example /weather/?date=2019-06-11.
		When this parameter is present, only the records with the matching date are returned.
	- accepts an optional query string parameter, city, and when this parameter is present, only the records with the
	matching city are returned. The value of this parameter is case insensitive, so "London" and "london" are equivalent.
	Moreover, it might contain several values, separated by commas (e.g. city=london,Kyiv), meaning that records with the
	city matching any of these values must be returned.
	- accepts an optional query string parameter, sort, that can take one of two values: either "date" or "-date". If the
	value is "date", then the ordering is by date in ascending order. If it is "-date", then the ordering is by date in
	descending order. If there are two records with the same date, the one with the smaller id must come first.
	 */

	@GetMapping
	public ResponseEntity<?> search(@RequestParam(value = "date", required = false) String date,
	                                @RequestParam(value = "city", required = false) String city,
	                                @RequestParam(value = "sort", required = false) String sort) {
		List<WeatherDto> searchResult = weatherService.search(date, decode(city), sort);
		return ResponseEntity.ok(searchResult);
	}

	private String decode(String value) {
		if (value == null) {
			return null;
		}
		try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to decode value " + value, e);
		}
	}

}
