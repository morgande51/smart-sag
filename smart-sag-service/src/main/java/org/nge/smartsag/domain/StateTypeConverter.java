package org.nge.smartsag.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StateTypeConverter implements AttributeConverter<StateType, String> {

	@Override
	public String convertToDatabaseColumn(StateType state) {
		return state.name();
	}

	@Override
	public StateType convertToEntityAttribute(String data) {
		return StateType.valueOf(data.toUpperCase());
	}
}