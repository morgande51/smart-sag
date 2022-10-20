package org.nge.smartsag.domain;

@FunctionalInterface
public interface IdentifiableDomain<T> {

	T getId();
}