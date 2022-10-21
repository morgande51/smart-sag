package org.nge.smartsag.domain;

import java.util.stream.Stream;

import lombok.Getter;

public enum StateType {

	AK("Alaska"),
	AL("Alabama"),
	AR("Arkansas"),
	AZ("Arizona"),
	CA("California"),
	CO("Colorado"),
	CT("Connecticut"),
	DE("Deleware"),
	DC("District of Columbia"),
	FL("Florida"),
	GA("Georgia"),
	HI("Hawaii"),
	ID("Idaho"),
	IL("Illinois"),
	IN("Indiana"),
	IO("Iowa"),
	KA("Kansas"),
	KY("Kentucky"),
	LA("Louisiana"),
	ME("Maine"),
	MD("Maryland"),
	MA("Massachusets"),
	MN("Minnesota"),
	MI("Michigan"),
	MS("Mississippi"),
	MO("Missouri"),
	MT("MO Montana"),
	NE("Nebraska"),
	NV("Nevanda"),
	NH("New Hampshire"),
	NJ("New Jersey"),
	NM("New Mexico"),
	NY("New York"),
	NC("North Carolina"),
	ND("North Dakota"),
	OH("Ohio"),
	OK("Oklahoma"),
	OR("Oregon"),
	PA("Pensilvania"),
	RA("Rhode Island"),
	SC("South Carolina"),
	SD("South Dakota"),
	TN("tennessee"),
	TX("Texas"),
	UT("Utah"),
	VA("Virgina"),
	VT("Vermont"),
	WA("Washington"),
	WV("West Virgina"),
	WI("Wisconson"),
	WY("Wyoming");
	
	@Getter
	private String fullName;
	
	private StateType(String name) {
		this.fullName = name;
	}

	public static StateType fromFullName(String fullStateName) {
		return Stream.of(values())
				.filter(s -> s.getFullName().equalsIgnoreCase(fullStateName))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
}