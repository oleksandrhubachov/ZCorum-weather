package com.zcorum.weather.service.impl;

import com.zcorum.weather.dto.WeatherDto;
import com.zcorum.weather.entity.WeatherEntity;
import com.zcorum.weather.repository.WeatherRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.zcorum.weather.constants.Constants.FIELD_CITY;
import static com.zcorum.weather.constants.Constants.FIELD_ID;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WeatherServiceImplTest {

	private WeatherServiceImpl weatherServiceImpl;

	@Mock
	private WeatherRepository weatherRepository;

	@Before
	public void setup() {
		this.weatherServiceImpl = new WeatherServiceImpl(weatherRepository);
	}

	@Test
	public void testCreateNullWeatherEntity() {
		assertNull(weatherServiceImpl.create(null));
		verify(weatherRepository, times(0)).save(any());
	}

	@Test
	public void testCreateWeatherEntity() {
		Integer id = 1;
		WeatherEntity entity = generateTestEntity(id);
		given(weatherRepository.save(any())).willReturn(entity);

		WeatherDto createdDto = weatherServiceImpl.create(generateTestDto());
		assertEntityValuesMatchingDtoValues(entity, createdDto);

		verify(weatherRepository, times(1)).save(any());
	}

	@Test
	public void testFindByNullId() {
		assertNull(weatherServiceImpl.findById(null));
		verify(weatherRepository, times(0)).findById(any());
	}

	@Test
	public void testFindByExistingId() {
		Integer id = 1;
		WeatherEntity entity = generateTestEntity(1);
		given(weatherRepository.findById(id)).willReturn(Optional.of(entity));

		WeatherDto dto = weatherServiceImpl.findById(id);
		assertEntityValuesMatchingDtoValues(entity, dto);

		verify(weatherRepository, times(1)).findById(any());
	}

	@Test
	public void testFindByNotExistingId() {
		Integer id = 1;
		given(weatherRepository.findById(any())).willReturn(Optional.empty());

		assertNull(weatherServiceImpl.findById(id));

		verify(weatherRepository, times(1)).findById(any());
	}

	@Test
	public void testSearchOrderByParameterAsc() {
		List<WeatherEntity> entitiesList = asList(generateTestEntity(1), generateTestEntity(2));
		given(weatherRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(entitiesList);

		List<WeatherDto> searchResult = weatherServiceImpl.search("2023-03-24", "Jersey City", "city");
		assertEquals(entitiesList.size(), searchResult.size());

		verify(weatherRepository, times(1)).findAll(any(Specification.class), eq(Sort.by(Sort.Direction.ASC, FIELD_CITY)));
	}

	@Test
	public void testSearchOrderByParameterDesc() {
		List<WeatherEntity> entitiesList = asList(generateTestEntity(1), generateTestEntity(2));
		given(weatherRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(entitiesList);

		List<WeatherDto> searchResult = weatherServiceImpl.search("2023-03-24", "Jersey City", "-city");
		assertEquals(entitiesList.size(), searchResult.size());

		verify(weatherRepository, times(1)).findAll(any(Specification.class), eq(Sort.by(Sort.Direction.DESC, FIELD_CITY)));
	}

	@Test(expected = RuntimeException.class)
	public void testSearchSortByUnknownField() {
		weatherServiceImpl.search("2023-03-24", "Jersey City", "unknown");

		verify(weatherRepository, times(0)).findAll(any(Specification.class), any(Sort.class));
	}

	@Test
	public void testSearchByParameters() {
		List<WeatherEntity> entitiesList = asList(generateTestEntity(1), generateTestEntity(2));
		given(weatherRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(entitiesList);

		String city = "Jersey City";
		String date = "2023-03-24";
		List<WeatherDto> searchResult = weatherServiceImpl.search(date, city, null);
		assertEquals(entitiesList.size(), searchResult.size());

		verify(weatherRepository, times(1)).findAll(
				any(Specification.class),
				eq(Sort.by(Sort.Direction.ASC, FIELD_ID)));
	}

	private void assertEntityValuesMatchingDtoValues(WeatherEntity entity, WeatherDto createdDto) {
		assertEquals(entity.getId(), createdDto.getId());
		assertEquals(entity.getCity(), createdDto.getCity());
		assertEquals(entity.getState(), createdDto.getState());
		assertEquals(entity.getDate(), createdDto.getDate());
		assertEquals(entity.getLon(), createdDto.getLon());
		assertEquals(entity.getLat(), createdDto.getLat());
		assertEquals(entity.getTemperatures(), createdDto.getTemperatures());
	}

	private WeatherDto generateTestDto() {
		WeatherDto dto = new WeatherDto();
		dto.setLon(-14.1234);
		dto.setLat(23.4567);
		dto.setDate(new Date());
		dto.setCity("Jersey City");
		dto.setState("New Jersey");
		dto.setTemperatures(asList(17.3, 16.8, 16.4, 16.0, 15.6, 15.3, 15.0, 14.9, 15.8, 18.0, 20.2, 22.3, 23.8, 24.9, 25.5, 25.7, 24.9, 23.0, 21.7, 20.8, 29.9, 29.2, 28.6, 28.1));
		return dto;
	}

	private WeatherEntity generateTestEntity(Integer id) {
		WeatherEntity entity = new WeatherEntity();
		entity.setId(Optional.ofNullable(id).orElse(1));
		entity.setLon(-14.1234);
		entity.setLat(23.4567);
		entity.setDate(new Date());
		entity.setCity("Jersey City");
		entity.setState("New Jersey");
		entity.setTemperatures(asList(17.3, 16.8, 16.4, 16.0, 15.6, 15.3, 15.0, 14.9, 15.8, 18.0, 20.2, 22.3, 23.8, 24.9, 25.5, 25.7, 24.9, 23.0, 21.7, 20.8, 29.9, 29.2, 28.6, 28.1));
		return entity;
	}

}