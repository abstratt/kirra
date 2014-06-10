package com.abstratt.kirra.rest.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import com.google.gson.Gson;

public class RestClient {

	private HttpClient httpClient;

	public RestClient() {
		this.httpClient = new HttpClient();
	}
	
	public <T> T executeMethod(HttpMethod method, Type type, int... acceptedStatuses) {
		try {
			System.out.println(method.getName() + " - " + method.getURI());
			int response = httpClient.executeMethod(method);
			boolean valid = acceptedStatuses.length == 0;
			for (int i = 0; !valid && i < acceptedStatuses.length; i++)
				valid = acceptedStatuses[i] == response;
			if (!valid)
				throw new RuntimeException("Unexpected status code: " + response + " - expected: " + Arrays.asList(acceptedStatuses).toString());
			byte [] responseBody = method.getResponseBody();
			System.out.println(new String(responseBody));
			return new Gson().fromJson(new InputStreamReader(new ByteArrayInputStream(responseBody)), type);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			method.releaseConnection();
		}
	}

}