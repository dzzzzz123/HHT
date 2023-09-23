// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PropertiesHelper.java

package ext.signature;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import wt.log4j.LogR;

public class PropertiesHelper {

	private static final Logger logger = LogR.getLogger(PropertiesHelper.class.getName());
	static String PROPERTIESPATH;

	public PropertiesHelper() {
	}

	public static String getStrFromProperties(String key) {
		String strinfo = "";
		try {
			String propertiefile = (new StringBuilder(String.valueOf(PROPERTIESPATH))).append("signature.properties")
					.toString();
			Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(propertiefile.substring(6, propertiefile.length())),
					"UTF-8"));
			strinfo = p.getProperty(key);
			if (strinfo != null)
				strinfo = strinfo.trim();
			logger.debug((new StringBuilder("Load signature.properties-->>")).append(key).append(":").append(strinfo)
					.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strinfo;
	}

	static {
		try {
			PROPERTIESPATH = "/" + PropertiesHelper.class.getResource("").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
