package com.zcorum.weather.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcorum.weather.dto.WeatherDto;
import com.zcorum.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.zcorum.weather.constants.Constants.yyyy_MM_dd;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private WeatherService weatherService;

	@Test
	public void createWeatherEndpointShouldReturn_201_StatusAndRecordId() throws Exception {
		WeatherDto request = generateTestWeatherRecord();
		WeatherDto expectedResponse = generateTestWeatherRecord();
		expectedResponse.setId(1);

		given(weatherService.create(any())).willReturn(expectedResponse);

		mockMvc.perform(post("/weather")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJsonString(request))
				)
				// verify status is 201 - Created
				.andExpect(status().isCreated())
				// verify all fields are present in response body, including ID
				.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
				.andExpect(jsonPath("$.lon").value(expectedResponse.getLon()))
				.andExpect(jsonPath("$.lat").value(expectedResponse.getLat()))
				.andExpect(jsonPath("$.city").value(expectedResponse.getCity()))
				.andExpect(jsonPath("$.state").value(expectedResponse.getState()))
				.andExpect(jsonPath("$.date").value(new SimpleDateFormat(yyyy_MM_dd).format(expectedResponse.getDate())))
				.andExpect(jsonPath("$.temperatures.length()").value(expectedResponse.getTemperatures().size()));
		// verify service was called only once
		verify(weatherService, times(1)).create(any());
	}

	@Test
	public void getWeatherRecordByExistingId() throws Exception {
		Integer expectedId = 1;
		WeatherDto expectedResponse = generateTestWeatherRecord();
		expectedResponse.setId(expectedId);
		given(weatherService.findById(expectedId)).willReturn(expectedResponse);
		mockMvc.perform(get("/weather/" + expectedId))
				// verify status is 200 - OK
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(expectedResponse.getId()))
				// verify all fields are present in response body, including ID
				.andExpect(jsonPath("$.lon").value(expectedResponse.getLon()))
				.andExpect(jsonPath("$.lat").value(expectedResponse.getLat()))
				.andExpect(jsonPath("$.city").value(expectedResponse.getCity()))
				.andExpect(jsonPath("$.state").value(expectedResponse.getState()))
				.andExpect(jsonPath("$.date").value(new SimpleDateFormat(yyyy_MM_dd).format(expectedResponse.getDate())))
				.andExpect(jsonPath("$.temperatures.length()").value(expectedResponse.getTemperatures().size()));
		// verify service was called only once
		verify(weatherService, times(1)).findById(expectedId);
	}

	@Test
	public void getWeatherRecordByNotExistingId() throws Exception {
		Integer expectedId = 100500;
		given(weatherService.findById(expectedId)).willReturn(null);
		mockMvc.perform(get("/weather/" + expectedId))
				// verify status is 404 - Not Found
				.andExpect(status().isNotFound())
				// verify record is not present in response body
				.andExpect(jsonPath("$").doesNotExist());
		// verify service was called only once
		verify(weatherService, times(1)).findById(expectedId);
	}

	@Test
	public void searchUsingOptionalParameters() throws Exception {
		// in this test we need to make sure that service is calling using correct parameters
		WeatherDto record = generateTestWeatherRecord();
		record.setId(1);
		given(weatherService.search(any(), any(), any())).willReturn(singletonList(record));
		String city = "Jersey City";
		String date = "2023-03-23";
		String sort = "-date";
		// call without params
		mockMvc.perform(get("/weather"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
		// call with city param
		mockMvc.perform(get("/weather").param("city", city))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
		// call with date param
		mockMvc.perform(get("/weather").param("date", date))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
		// call with sort param
		mockMvc.perform(get("/weather").param("sort", sort))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
		// call with all params
		mockMvc.perform(get("/weather")
						.param("city", city)
						.param("date", date)
						.param("sort", sort))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
		verify(weatherService, times(1)).search(null, null, null);
		verify(weatherService, times(1)).search(null, city, null);
		verify(weatherService, times(1)).search(date, null, null);
		verify(weatherService, times(1)).search(null, null, sort);
		verify(weatherService, times(1)).search(date, city, sort);
	}

	private WeatherDto generateTestWeatherRecord() {
		WeatherDto record = new WeatherDto();
		record.setLon(-14.1234);
		record.setLat(23.4567);
		record.setDate(new Date());
		record.setCity("Jersey City");
		record.setState("New Jersey");
		record.setTemperatures(asList(17.3, 16.8, 16.4, 16.0, 15.6, 15.3, 15.0, 14.9, 15.8, 18.0, 20.2, 22.3, 23.8, 24.9, 25.5, 25.7, 24.9, 23.0, 21.7, 20.8, 29.9, 29.2, 28.6, 28.1));
		return record;
	}

	private String toJsonString(WeatherDto obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("Unable to convert POJO to JSON string", e);
		}
	}
}