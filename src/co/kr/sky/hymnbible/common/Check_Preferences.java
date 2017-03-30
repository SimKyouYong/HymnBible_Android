package co.kr.sky.hymnbible.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class Check_Preferences{
	  public static void setAppPreferences(Activity context, String key, String value)
	  {
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    SharedPreferences.Editor prefEditor = pref.edit();
	    prefEditor.putString(key, value);  
	    prefEditor.commit();
	  }
	  public static void setAppPreferences(Context context, String key, String value)
	  {
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    SharedPreferences.Editor prefEditor = pref.edit();
	    prefEditor.putString(key, value);  
	    prefEditor.commit();
	  }
	  public static void setAppPreferences(Activity context, String key, Boolean value)
	  {
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    SharedPreferences.Editor prefEditor = pref.edit();
	    prefEditor.putBoolean(key, value);  
	    prefEditor.commit();
	  }
	  public static String getAppPreferences(Context context, String key)
	  {
	    String returnValue = null;
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    returnValue = pref.getString(key, "");
	    return returnValue;
	  }
	  public static String getAppPreferenceson(Context context, String key)
	  {
	    String returnValue = null;
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    returnValue = pref.getString(key, "on");
	    return returnValue;
	  }
	  public static Boolean getAppPreferencesboolean(Context context, String key)
	  {
	    Boolean returnValue = false;
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    returnValue = pref.getBoolean(key, false);
	    return returnValue;
	  }
	  
	  public static void setDBVersion(Activity context, String version, String value)
	  {
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    SharedPreferences.Editor prefEditor = pref.edit();
	    prefEditor.putString(version, value);  
	    prefEditor.commit();
	  }
	  
	  public static String getDBVersion(Context context, String version)
	  {
	    String returnValue = null;
	    SharedPreferences pref = null;
	    pref = context.getSharedPreferences("Check_Shared", 0);
	    returnValue = pref.getString(version, "");
	    return returnValue;
	  }
	  
	}
