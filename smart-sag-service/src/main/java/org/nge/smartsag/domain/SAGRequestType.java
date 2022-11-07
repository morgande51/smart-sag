package org.nge.smartsag.domain;

import java.util.stream.Stream;

import lombok.Getter;

public enum SAGRequestType {

	FLAT('F'), MECHANICAL('M'), INJURY('I'), OTHER('O');
	
	@Getter
	Character code;
	
	private SAGRequestType(char code) {
		this.code = code;
	}
	
	public static SAGRequestType from(Character data) {
		return Stream.of(values())
				.filter(t -> t.code.equals(data))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
}
