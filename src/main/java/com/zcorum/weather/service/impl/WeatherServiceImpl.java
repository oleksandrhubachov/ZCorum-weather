package com.zcorum.weather.service.impl;

import com.zcorum.weather.dto.WeatherDto;
import com.zcorum.weather.entity.WeatherEntity;
import com.zcorum.weather.exception.WrongDateFormatException;
import com.zcorum.weather.mapper.WeatherEntityDtoMapper;
import com.zcorum.weather.repository.WeatherRepository;
import com.zcorum.weather.service.WeatherService;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.zcorum.weather.constants.Constants.FIELD_CITY;
import static com.zcorum.weather.constants.Constants.FIELD_DATE;
import static com.zcorum.weather.constants.Constants.FIELD_ID;
import static com.zcorum.weather.constants.Constants.yyyy_MM_dd;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class WeatherServiceImpl implements WeatherService {

	private final WeatherRepository weatherRepository;

	private final WeatherEntityDtoMapper mapper;

	public WeatherServiceImpl(WeatherRepository weatherRepository) {
		this.weatherRepository = weatherRepository;
		this.mapper = Mappers.getMapper(WeatherEntityDtoMapper.class);
	}

	@Override
	public WeatherDto create(WeatherDto request) {
		if (request == null) {
			return null;
		}
		WeatherEntity entity = mapper.dtoToEntity(request);
		entity = weatherRepository.save(entity);
		return mapper.entityToDto(entity);
	}

	@Override
	public WeatherDto findById(Integer id) {
		if (id == null) {
			return null;
		}
		Optional<WeatherEntity> entity = weatherRepository.findById(id);
		return entity.map(mapper::entityToDto).orElse(null);
	}

	@Override
	public List<WeatherDto> search(String date, String city, String sort) {
		Specification<WeatherEntity> query = constructSearchQueryConditions(date, city);
		Sort orderBy = constructOrderBy(sort);
		return mapper.entitiesToDtos(weatherRepository.findAll(query, orderBy));
	}

	private Sort constructOrderBy(String sort) {
		Sort orderBy = Sort.by(Sort.Direction.ASC, FIELD_ID);
		if (!StringUtils.isEmpty(sort)) {
			Sort.Direction direction = sort.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
			sort = sort.replaceAll("-", "");
			if (!sort.equals(FIELD_CITY) && !sort.equals(FIELD_DATE)) {
				throw new RuntimeException("Unknown sort field " + sort);
			}
			orderBy = Sort.by(direction, sort);
		}
		return orderBy;
	}

	private Specification<WeatherEntity> constructSearchQueryConditions(String date, String city) {
		return (root, cq, cb) -> {
			Predicate predicate = null;
			if (!StringUtils.isEmpty(date)) {
				predicate = cb.equal(root.get(FIELD_DATE), convertStrToDate(date));
			}
			if (!StringUtils.isEmpty(city)) {
				List<String> cities = splitCities(city);
				Predicate condition;
				if (cities.size() == 1) {
					condition = cb.equal(cb.lower(root.get(FIELD_CITY)), city.toLowerCase());
				} else {
					condition = cb.lower(root.get(FIELD_CITY)).in(cities);
				}
				predicate = predicate == null ? condition : cb.and(predicate, condition);
			}
			return predicate;
		};
	}

	private List<String> splitCities(String city) {
		String[] split = city.split(",");
		if (split.length == 1) {
			return singletonList(city);
		}
		return Arrays.stream(split).map(String::toLowerCase).collect(toList());
	}

	private Date convertStrToDate(String date) {
		try {
			return new SimpleDateFormat(yyyy_MM_dd).parse(date);
		} catch (ParseException e) {
			throw new WrongDateFormatException("Unable to parse str " + date + " to date", e);
		}
	}

}
