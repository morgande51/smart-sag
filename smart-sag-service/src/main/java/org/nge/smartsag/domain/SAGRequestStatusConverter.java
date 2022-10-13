package org.nge.smartsag.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SAGRequestStatusConverter implements AttributeConverter<SAGRequestStatus, Character> {

	@Override
	public Character convertToDatabaseColumn(SAGRequestStatus status) {
		return status.getCode();
	}

	@Override
	public SAGRequestStatus convertToEntityAttribute(Character data) {
		return SAGRequestStatus.from(data);
	}
}