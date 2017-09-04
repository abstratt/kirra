package com.abstratt.kirra;

import java.util.LinkedHashMap;
import java.util.Map;

public class Blob {
	private final String token;
	private final long contentLength;
	private final String contentType;
	private final String originalName;
	public String getOriginalName() {
		return originalName;
	}
	public String getToken() {
		return token;
	}
	public long getContentLength() {
		return contentLength;
	}
	public String getContentType() {
		return contentType;
	}
	public Blob(String token, long contentLength, String contentType, String originalName) {
		super();
		this.token = token;
		this.contentLength = contentLength;
		this.contentType = contentType;
		this.originalName = originalName;
	}
	public Map<String, Object> toMap() {
		Map<String, Object> asMap = new LinkedHashMap<>();
		asMap.put("token", this.token);
		asMap.put("contentType", contentType);
		asMap.put("originalName", originalName);
		asMap.put("contentLength", contentLength);
		return asMap;
	}
}
