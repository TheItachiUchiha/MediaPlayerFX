package com.ita.util;

import com.ita.ui.ErrorDialog;

import java.io.InputStream;
import java.util.*;

public class PropertiesUtils {
	public static Map<String, String> readDetails()
	{
		Map<String, String> map = new HashMap<String, String>();
		try{
			InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("details.properties");
			Properties p = new Properties();
			p.load(in);
			map.put("name", p.getProperty("name"));
			map.put("version", p.getProperty("version"));
			map.put("link", p.getProperty("link"));
		}
		catch (Exception e) {
			new ErrorDialog(e).show();
		}
		return map;
	}
	
	public static List<String> readFormats()
	{
		List<String> formats = null;
		try
		{
			InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("details.properties");
			Properties p = new Properties();
			p.load(in);
			formats = Arrays.asList(p.getProperty("formats").split(","));
		}
		catch (Exception e) {
			new ErrorDialog(e).show();
		}
		return formats;
	}
}
