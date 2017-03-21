package co.kr.sky.hymnbible.obj;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MyPhoneObj implements Parcelable{
	public static Parcelable.Creator<MyPhoneObj> getCreator() {
		return CREATOR;
	}	
	String _ID;
	String TITLE;
	String ACCOUNT_NAME;
	String ACCOUNT_TYPE;
	String DELETED;
	String GROUP_VISIBLE;
	String GROUP_COUNT;
	String CHECK;
	
	public MyPhoneObj(String _ID, String tITLE, String aCCOUNT_NAME, String aCCOUNT_TYPE, String dELETED,
			String gROUP_VISIBLE, String gROUP_COUNT, String cHECK) {
		super();
		this._ID = _ID;
		TITLE = tITLE;
		ACCOUNT_NAME = aCCOUNT_NAME;
		ACCOUNT_TYPE = aCCOUNT_TYPE;
		DELETED = dELETED;
		GROUP_VISIBLE = gROUP_VISIBLE;
		GROUP_COUNT = gROUP_COUNT;
		CHECK = cHECK;
	}
	public String get_ID() {
		return _ID;
	}
	public void set_ID(String _ID) {
		this._ID = _ID;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public String getACCOUNT_NAME() {
		return ACCOUNT_NAME;
	}
	public void setACCOUNT_NAME(String aCCOUNT_NAME) {
		ACCOUNT_NAME = aCCOUNT_NAME;
	}
	public String getACCOUNT_TYPE() {
		return ACCOUNT_TYPE;
	}
	public void setACCOUNT_TYPE(String aCCOUNT_TYPE) {
		ACCOUNT_TYPE = aCCOUNT_TYPE;
	}
	public String getDELETED() {
		return DELETED;
	}
	public void setDELETED(String dELETED) {
		DELETED = dELETED;
	}
	public String getGROUP_VISIBLE() {
		return GROUP_VISIBLE;
	}
	public void setGROUP_VISIBLE(String gROUP_VISIBLE) {
		GROUP_VISIBLE = gROUP_VISIBLE;
	}
	public String getGROUP_COUNT() {
		return GROUP_COUNT;
	}
	public void setGROUP_COUNT(String gROUP_COUNT) {
		GROUP_COUNT = gROUP_COUNT;
	}
	public String getCHECK() {
		return CHECK;
	}
	public void setCHECK(String cHECK) {
		CHECK = cHECK;
	}
	public MyPhoneObj(Parcel in) {
		readFromParcel(in);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(_ID);
		dest.writeString(TITLE);
		dest.writeString(ACCOUNT_NAME);
		dest.writeString(ACCOUNT_TYPE);
		dest.writeString(DELETED);
		dest.writeString(GROUP_VISIBLE);
		dest.writeString(GROUP_COUNT);
		dest.writeString(CHECK);

	}
	private void readFromParcel(Parcel in){
		_ID = in.readString();
		TITLE = in.readString();
		ACCOUNT_NAME = in.readString();
		ACCOUNT_TYPE = in.readString();
		DELETED = in.readString();
		GROUP_VISIBLE = in.readString();
		GROUP_COUNT = in.readString();
		CHECK = in.readString();

	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<MyPhoneObj> CREATOR = new Parcelable.Creator() {
		public Object createFromParcel(Parcel in) {
			return new MyPhoneObj(in);
		}

		public Object[] newArray(int size) {
			return new MyPhoneObj[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
