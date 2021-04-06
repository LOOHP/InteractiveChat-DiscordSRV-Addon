package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class URLRequestUtils {
	
	public static InputStream getInputStream(String link) {
		InputStream stream = getInputStream0(link);
		return stream == null ? new ByteArrayInputStream(new byte[0]) : stream;
	}
	
	public static InputStream getInputStream0(String link) {
		URLConnection connection;
		try {
			connection = new URL(link).openConnection();
			connection.setUseCaches(false);
	        connection.setDefaultUseCaches(false);
	        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
	        connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
	        connection.addRequestProperty("Pragma", "no-cache");
	        return connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream retrieveInputStreamUntilSuccessful(List<ThrowingSupplier<InputStream>> sources) {
		InputStream stream = null;
		boolean success = false;
		int i = 0;
		while (!success && i < sources.size()) {
			try {
				stream = sources.get(i).get();
				if (stream != null) {
					success = true;
				}
			} catch (Throwable e) {}
			i++;
		}
		return stream;
	}

}
