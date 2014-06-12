package com.abstratt.kirra.fixtures;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.KirraException.Kind;
import com.google.gson.Gson;

public class FixtureHelper {
	public static <T> T loadFixture(Type type, String... segments) {
		return loadFixture(true, type, segments);
	}
	private static <T> T loadFixture(boolean tryVariants, Type type, String... segments) {
		String relativePath = StringUtils.join(segments, "/");
		String resourcePath = "/fixtures/" + relativePath;
		System.out.print("Loading fixtures from " + resourcePath + "... ");
		try (InputStream contents = FixtureHelper.class.getResourceAsStream(resourcePath)) {
			if (contents == null) {
				if (tryVariants && segments.length > 0) {
					segments[segments.length-1] += ".json"; 
					return loadFixture(false, type, segments);
				}
				System.out.println("Not found");
				return null;
			}
			System.out.println("OK!");
			return new Gson().fromJson(new InputStreamReader(contents), type);
		} catch (IOException e) {
			throw new KirraException("Unexpected", e, Kind.SCHEMA);
		}
	}

}
