package com.abstratt.kirra.rest.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jackson.JsonNode;

public class RestHelper {

	private HttpClient httpClient;
	private URI baseUri;

	public RestHelper(URI baseUri) {
		httpClient = new HttpClient();
		this.baseUri = baseUri;
	}

	public HttpMethod buildGetRequest(URI location) throws HttpException,
			IOException {
		return new GetMethod(location.toASCIIString());
	}

	public byte[] executeMethod(HttpMethod method, int... accepted)
			throws IOException, HttpException {
		try {
			int response = httpClient.executeMethod(method);
			byte[] body = method.getResponseBody();
			return body;
		} finally {
			method.releaseConnection();
		}
	}
	
	public <T extends JsonNode> T executeJsonMethod(HttpMethod method)
			throws IOException, HttpException {
		return (T) JsonHelper.parse(new InputStreamReader(new ByteArrayInputStream(executeMethod(method))));
	}

	public JsonNode get(String relativeUri, int... accepted) {
		GetMethod getMethod = new GetMethod(baseUri.resolve(relativeUri).toString());
		executeMethod(getMethod, accepted);
	}
	
	public JsonNode getList(String relativeUri, int... accepted) {
		GetMethod getMethod = new GetMethod(baseUri.resolve(relativeUri).toString());
		
		executeMethod(getMethod, accepted);
	}

}
