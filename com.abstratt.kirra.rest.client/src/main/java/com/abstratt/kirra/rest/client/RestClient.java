package com.abstratt.kirra.rest.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.ErrorDTO;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class RestClient {

    private HttpClient httpClient;
	private URI baseURI;
	private String username;
	private String password;

    public RestClient() {
        this(URI.create(""));
    }
    
    public RestClient(URI baseURI) {
        this.httpClient = createHttpClient();
        this.baseURI = baseURI;
    }

    public <T> T executeMethod(HttpMethod method, Type resultType, Integer... acceptedStatuses) {
        try {
        	if (username != null) {
        		String encodedCredentials = new String(Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8")));
        		method.addRequestHeader(new Header("Authorization", "Basic " + encodedCredentials));
        	}
            System.out.println(method.getName() + " - " + method.getPath());
            if (method instanceof EntityEnclosingMethod)
                ((EntityEnclosingMethod) method).getRequestEntity().writeRequest(System.out);
            int response = httpClient.executeMethod(method);
            System.out.println("Request headers: " + Arrays.stream(method.getRequestHeaders()).map(it -> it.getName() + " -> " + it.getValue()).reduce("", (a, b) -> a + "\n" + b));
            System.out.println("Status: " + response + " - accepted: " + Arrays.asList(acceptedStatuses));
            System.out.println("Response headers: " + Arrays.stream(method.getResponseHeaders()).map(it -> it.getName() + " -> " + it.getValue()).reduce("", (a, b) -> a + "\n" + b));

            boolean valid = acceptedStatuses.length == 0;
            for (int i = 0; !valid && i < acceptedStatuses.length; i++)
                valid = acceptedStatuses[i] == null || acceptedStatuses[i] == response;
            String responseBody = method.getResponseBodyAsString();
            System.out.println(responseBody);
            if (!valid)
				throwException(response, responseBody, acceptedStatuses);
            return getGson().fromJson(new StringReader(responseBody), resultType);
        } catch (JsonParseException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            method.releaseConnection();
        }
    }

	private void throwException(int response, String responseBody, Integer... acceptedStatuses) {
		ErrorDTO error = getGson().fromJson(responseBody, ErrorDTO.class);
		throw new KirraException(error.getMessage(), error.getKind());
	}
	
    public <T> T get(URI baseUri, Type type, String... segments) {
        return get(baseUri, type, Arrays.asList(segments), Collections.<String, List<String>>emptyMap());
    }
    public <T> T get(URI baseUri, Type type, List<String> segments, Map<String, List<String>> query) {
        GetMethod get = new GetMethod(baseUri.resolve(StringUtils.join(segments, "/")).toString());
        return executeMethod(get, type);
    }
    
    public <T> T get(URI baseUri, Type type, List<String> segments) {
        return get(baseUri, type, segments, Collections.<String, List<String>>emptyMap());
    }
    
    public <T> T put(URI baseUri, T toUpdate, String... segments) {
        PutMethod put = new PutMethod(baseUri.resolve(StringUtils.join(segments, "/")).toString());
        String asJson = getGson().toJson(toUpdate);
        try {
            put.setRequestEntity(new StringRequestEntity(asJson, "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return executeMethod(put, toUpdate.getClass());
    }

	private Gson getGson() {
		return CommonHelper.buildGson(baseURI).create();
	}

    public <T> T post(URI baseUri, Object toCreate, Type resultType, String... segments) {
        PostMethod post = new PostMethod(baseUri.resolve(StringUtils.join(segments, "/")).toString());
        String asJson = getGson().toJson(toCreate);
        try {
            post.setRequestEntity(new StringRequestEntity(asJson, "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return executeMethod(post, resultType, HttpStatus.SC_CREATED, HttpStatus.SC_ACCEPTED, HttpStatus.SC_OK);
    }
    
    public void clearCredentials() {
    	this.httpClient = createHttpClient();
    }
    
	public void setCredentials(URI baseUri, String realm, String username, String password) {
		this.username = username;
		this.password = password;
		this.httpClient = createHttpClient();
	}

	private HttpClient createHttpClient() {
		return new HttpClient();
	}

}
