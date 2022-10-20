package org.nge.smartsag.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.nge.smartsag.domain.IdentifiableDomain;

import lombok.Getter;

@Getter
public class ChildrenReference<T, D extends IdentifiableDomain<T>> {
	
	private String type;
	
	private List<T> ids;
	
	public ChildrenReference(Collection<D> domains, String type) {
		ids = domains.stream().map(IdentifiableDomain::getId).collect(Collectors.toList());
		this.type = type;
	}
}