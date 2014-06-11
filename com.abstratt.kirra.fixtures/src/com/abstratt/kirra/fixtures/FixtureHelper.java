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
		String relativePath = StringUtils.join(segments, "/");
		String resourcePath = "/fixtures/" + relativePath + ".json";
		System.out.print("Loading fixtures from " + resourcePath + "... ");
		try (InputStream contents = FixtureHelper.class.getResourceAsStream(resourcePath)) {
			if (contents == null) {
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
