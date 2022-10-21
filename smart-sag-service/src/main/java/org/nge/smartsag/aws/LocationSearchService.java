package org.nge.smartsag.aws;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.nge.smartsag.domain.Address;

import software.amazon.awssdk.services.location.LocationClient;
import software.amazon.awssdk.services.location.model.Place;
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextRequest;
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextResponse;

@ApplicationScoped
public class LocationSearchService {
	
	private static final Logger log = Logger.getLogger(LocationSearchService.class);
	
	private static final Set<String> TARGET_FIELDS = Stream.of("AddressNumber", "Street", "Municipality", "Region", "PostalCode").collect(Collectors.toSet());
	
	@ConfigProperty(name = "aws.location.place")
	String searchPlace;
	
	// TODO: get this from properties file
	@ConfigProperty(name = "aws.location.countries", defaultValue = "USA")
	String[] searchCountries;
	
	@Inject
	LocationClient locationClient;
	
	public Address getAddressFor(String location) {
		SearchPlaceIndexForTextRequest searchRequest = SearchPlaceIndexForTextRequest
				.builder()
				.maxResults(5)
				.filterCountries(searchCountries)
				.indexName(searchPlace)
				.text(location)
				.build();
		SearchPlaceIndexForTextResponse resp = locationClient.searchPlaceIndexForText(searchRequest);
		if (!resp.hasResults()) {
			// TODO: handle this
			throw new RuntimeException("No results for location: " + location);
		}
		else if (resp.results().size() > 1) {
			log.warnf("Location is ambiguous: %s", location);
			log.warnf("relevance: %s", resp.results().get(0).relevance());
		}
		Place place = getGeocodedPlace(resp);
		
		Double x = place.geometry().point().get(0);
		Double y = place.geometry().point().get(1);
		String strNumber = place.addressNumber();
		String strName = place.street();
		String city = place.municipality();
		String state = place.region();
		String zip = place.postalCode();
		Address addy = Address.from(strNumber, strName, city, state, zip, x, y);
		return addy;
	}
	
	protected Place getGeocodedPlace(SearchPlaceIndexForTextResponse resp) {
		String location = resp.summary().text();
		if (!resp.hasResults()) {
			// TODO: handle this
			throw new RuntimeException("No results for location: " + location);
		}
		else if (resp.results().size() > 1) {
			log.warnf("Location is ambiguous: %s", location);
		}
		
		Place place = resp.results().get(0).place();
		validateFields(place);
		return place;
	}
	
	protected void validateFields(Place place) {
		TARGET_FIELDS.forEach(f -> {
			String value = place.getValueForField(f, String.class).orElseThrow(() -> {
				// TODO: handle this
				throw new RuntimeException("No value for field: " + f);
			});
			log.debugf("Field: %s = %s", f, value);
		});
	}
}