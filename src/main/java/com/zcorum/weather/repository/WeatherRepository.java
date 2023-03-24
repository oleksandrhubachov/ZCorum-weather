package com.zcorum.weather.repository;

import com.zcorum.weather.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer>, JpaSpecificationExecutor<WeatherEntity> {
}
