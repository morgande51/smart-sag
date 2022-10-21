package org.nge.smartsag.aws;

import org.nge.smartsag.domain.Address;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.location.LocationClient;
import software.amazon.awssdk.services.location.model.Place;
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextRequest;
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextResponse;

public class SAGLocationServiceTest {

	private static String location = "602 Brunner Dr, Cincinnati OH, 45240";

	public static void main(String[] args) {
		String key = args[0];
		String secret = args[1];
		System.setProperty("aws.region", "us-east-2");
		AwsBasicCredentials creds = AwsBasicCredentials.create(key, secret);
		StaticCredentialsProvider provider = StaticCredentialsProvider.create(creds);
		LocationClient client =  LocationClient.builder().credentialsProvider(provider).build();
	
		SearchPlaceIndexForTextRequest searchRequest = SearchPlaceIndexForTextRequest
				.builder()
				.maxResults(5)
				.filterCountries("USA")
				.indexName("SmartSAG.place")
				.text(location)
				.build();
		
		SearchPlaceIndexForTextResponse resp = client.searchPlaceIndexForText(searchRequest);
		resp.results().forEach(r -> {
			r.sdkFields().forEach(f -> {
				System.out.println("Field name: " + f.locationName());
			});
			Place place = r.place();
			Double x = place.geometry().point().get(0);
			Double y = place.geometry().point().get(1);
			System.out.println("X: " + x + ", Y: " + y);
			String strNumber = place.addressNumber();
			String strName = place.street();
			String city = place.municipality();
			String state = place.region();
			String zip = place.postalCode();
			Address addy = Address.from(strNumber, strName, city, state, zip, x, y);
			System.out.println(addy);
		});
	}
}