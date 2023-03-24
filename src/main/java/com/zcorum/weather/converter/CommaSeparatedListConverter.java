package com.zcorum.weather.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class CommaSeparatedListConverter implements AttributeConverter<List<Double>, String> {

	private static final String DELIMITER = ",";

	@Override
	public String convertToDatabaseColumn(List<Double> list) {
		if (list == null) {
			return "";
		}
		return list.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(DELIMITER));
	}

	@Override
	public List<Double> convertToEntityAttribute(String joined) {
		if (joined == null) {
			return new ArrayList<>();
		}
		return Arrays.stream(joined.split(DELIMITER))
				.map(Double::valueOf)
				.collect(Collectors.toList());
	}

}
