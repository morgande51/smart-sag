package org.nge.smartsag.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SAGRequestStatusTypeConverter implements AttributeConverter<SAGRequestStatusType, Character> {

	@Override
	public Character convertToDatabaseColumn(SAGRequestStatusType status) {
		return status.getCode();
	}

	@Override
	public SAGRequestStatusType convertToEntityAttribute(Character data) {
		return SAGRequestStatusType.from(data);
	}
}