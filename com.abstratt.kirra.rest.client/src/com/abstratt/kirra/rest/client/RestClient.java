package com.abstratt.kirra.rest.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class RestClient {

	private HttpClient httpClient;

	public RestClient() {
		this.httpClient = new HttpClient();
	}
	
	public <T> T executeMethod(HttpMethod method, Type type, int... acceptedStatuses) {
		try {
			System.out.println(method.getName() + " - " + method.getURI());
			if (method instanceof EntityEnclosingMethod)
				((EntityEnclosingMethod) method).getRequestEntity().writeRequest(System.out);
			int response = httpClient.executeMethod(method);
			boolean valid = acceptedStatuses.length == 0;
			for (int i = 0; !valid && i < acceptedStatuses.length; i++)
				valid = acceptedStatuses[i] == response;
			if (!valid)
				throw new RuntimeException("Unexpected status code: " + response + " - expected: " + Arrays.asList(acceptedStatuses).toString());
			String responseBody = new String(method.getResponseBody());
			System.out.println(responseBody);
			return new Gson().fromJson(new StringReader(responseBody), type);
		} catch (JsonParseException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			method.releaseConnection();
		}
	}
	
	public <T> T get(URI baseUri, Type type, String... segments) {
		GetMethod get = new GetMethod(baseUri.resolve(StringUtils.join(segments, "/")).toString());
		return executeMethod(get, type);
	}
	
	public <T> T post(URI baseUri, T toCreate, String... segments) {
		PostMethod post = new PostMethod(baseUri.resolve(StringUtils.join(segments, "/")).toString());
		String asJson = new Gson().toJson(toCreate);
		try {
			post.setRequestEntity(new StringRequestEntity(asJson,"application/json", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return executeMethod(post, toCreate.getClass());
	}

}