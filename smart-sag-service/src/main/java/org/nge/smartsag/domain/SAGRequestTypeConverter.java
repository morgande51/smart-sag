package org.nge.smartsag.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SAGRequestTypeConverter implements AttributeConverter<SAGRequestType, Character> {

	@Override
	public Character convertToDatabaseColumn(SAGRequestType attribute) {
		return attribute.getCode();
	}

	@Override
	public SAGRequestType convertToEntityAttribute(Character data) {
		return SAGRequestType.from(data);
	}
}
