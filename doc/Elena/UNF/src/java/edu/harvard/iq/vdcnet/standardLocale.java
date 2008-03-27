package edu.harvard.iq.vdcnet;

import java.util.Locale;
import java.util.ResourceBundle;

public class standardLocale {
	private boolean restore = true;
	private boolean VCPP;
	private static String oldlocale;
	private static int ormode;
	ResourceBundle bundle;
	Locale target;
	String app = "com.sun.demo.intl.AppResource";
	public standardLocale(){
		target = new Locale("en", "US");
		bundle = ResourceBundle.getBundle(app,target);
		
	}
	public void standard_locale(boolean restore){
		if(VCPP){
			int curmode;
			int err;
		}
		
		if(restore){
			
		}
	}

}
