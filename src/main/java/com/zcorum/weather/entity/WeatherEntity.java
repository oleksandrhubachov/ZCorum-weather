package com.zcorum.weather.entity;

import com.zcorum.weather.converter.CommaSeparatedListConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "weather")
public class WeatherEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Temporal(TemporalType.DATE)
	@Column(name = "date")
	private Date date;

	@Column(name = "lat")
	private Double lat;

	@Column(name = "lon")
	private Double lon;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "temperatures")
	@Convert(converter = CommaSeparatedListConverter.class)
	private List<Double> temperatures;

}
