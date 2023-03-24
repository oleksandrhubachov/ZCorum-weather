package com.zcorum.weather;

import com.zcorum.weather.dto.WeatherDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Sql(scripts = {"/create-default-db-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WeatherApiIntegrationTest {

	@Value("${server.port}")
	private int serverPort;

	private String controllerUrl;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@BeforeEach
	public void before() {
		controllerUrl = "http://localhost:" + serverPort + "/weather";
	}

	private static Stream<Arguments> testData() {
		return Stream.of(
				Arguments.of(null, null, null, asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)),
				Arguments.of(null, "Laredo", null, asList(1, 2, 3, 4)),
				Arguments.of(null, "laredo", null, asList(1, 2, 3, 4)),
				Arguments.of(null, "LAREDO", null, asList(1, 2, 3, 4)),
				Arguments.of("2023-03-20", null, null, asList(1, 5)),
				Arguments.of("2023-03-21", "JACKSONVILLE", null, singletonList(6)),
				Arguments.of(null, null, "date", asList(1, 5, 2, 6, 3, 7, 4, 8, 9, 10, 11, 12)),
				Arguments.of(null, null, "-date", asList(12, 11, 10, 9, 4, 8, 3, 7, 2, 6, 1, 5)),
				Arguments.of("2023-03-21", null, "-city", asList(2, 6)),
				Arguments.of(null, "London", "-city", emptyList()),
				Arguments.of("2024-01-01", "Laredo", "city", emptyList()),
				Arguments.of(null, "Laredo,Los Angeles", "city", asList(1, 2, 3, 4, 9, 10, 11, 12))
		);
	}

	@ParameterizedTest
	@MethodSource("testData")
	public void test(String date, String city, String sort, List<Integer> expectedIds) {
		String url = constructSearchUrl(date, city, sort);
		ResponseEntity<WeatherDto[]> responseEntity = testRestTemplate.getForEntity(url, WeatherDto[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		List<Integer> foundIds = Arrays.stream(Objects.requireNonNull(responseEntity.getBody())).map(WeatherDto::getId).collect(toList());
		assertEquals(expectedIds, foundIds);
	}

	@Test
	public void testWrongDateFormatSearch() {
		String url = constructSearchUrl("January 1st, 2023", null, null);
		ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(url, String.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCodeValue());
	}

	@Test
	public void testGetExistingRecordById() {
		Integer id = 1;
		ResponseEntity<WeatherDto> responseEntity = testRestTemplate.getForEntity(controllerUrl + "/" + id, WeatherDto.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(id, responseEntity.getBody().getId());
	}

	@Test
	public void testGetNotExistingRecordById() {
		Integer id = 100500;
		ResponseEntity<WeatherDto> responseEntity = testRestTemplate.getForEntity(controllerUrl + "/" + id, WeatherDto.class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}

	@Test
	public void testGetRecordByWrongFormatId() {
		String id = "1a";
		ResponseEntity<WeatherDto> responseEntity = testRestTemplate.getForEntity(controllerUrl + "/" + id, WeatherDto.class);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}

	@Test
	public void testCreateRecord() {
		WeatherDto dto = generateDto();
		ResponseEntity<WeatherDto> responseEntity = testRestTemplate.postForEntity(controllerUrl, dto, WeatherDto.class);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody().getId());
		assertEquals(dto.getCity(), responseEntity.getBody().getCity());

		ResponseEntity<WeatherDto> getWeatherResponse = testRestTemplate.getForEntity(controllerUrl + "/" + responseEntity.getBody().getId(), WeatherDto.class);
		assertEquals(HttpStatus.OK, getWeatherResponse.getStatusCode());
		assertEquals(responseEntity.getBody().getId(), getWeatherResponse.getBody().getId());
	}

	private WeatherDto generateDto() {
		WeatherDto dto = new WeatherDto();
		dto.setDate(new Date());
		dto.setCity("Pittsburgh");
		dto.setState("Pennsylvania");
		dto.setLat(-2.78587);
		dto.setLon(139.66280);
		dto.setTemperatures(asList(33.5, 30.3, 31.9, 34.5, 33.0, 30.8, 31.9, 30.2, 33.7, 33.1, 30.2, 33.1, 33.8, 31.2, 33.2, 30.6, 34.5, 31.8, 33.1, 31.9, 31.2, 30.8, 34.5, 33.3));
		return dto;
	}

	private String constructSearchUrl(String date, String city, String sort) {
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(controllerUrl);
		if (date != null) {
			urlBuilder = urlBuilder.queryParam("date", date);
		}
		if (city != null) {
			urlBuilder = urlBuilder.queryParam("city", city);
		}
		if (sort != null) {
			urlBuilder = urlBuilder.queryParam("sort", sort);
		}
		return urlBuilder.toUriString();
	}


}
