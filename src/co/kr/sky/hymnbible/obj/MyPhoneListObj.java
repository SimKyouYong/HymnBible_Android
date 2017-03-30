package co.kr.sky.hymnbible.obj;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MyPhoneListObj implements Parcelable{
	public static Parcelable.Creator<MyPhoneListObj> getCreator() {
		return CREATOR;
	}	
	String NAME;
	String PHONE;
	
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String getPHONE() {
		return PHONE;
	}
	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}
	public MyPhoneListObj(String nAME, String pHONE) {
		super();
		NAME = nAME;
		PHONE = pHONE;
	}
	public MyPhoneListObj(Parcel in) {
		readFromParcel(in);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(NAME);
		dest.writeString(PHONE);

	}
	private void readFromParcel(Parcel in){
		NAME = in.readString();
		PHONE = in.readString();

	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<MyPhoneListObj> CREATOR = new Parcelable.Creator() {
		public Object createFromParcel(Parcel in) {
			return new MyPhoneListObj(in);
		}

		public Object[] newArray(int size) {
			return new MyPhoneListObj[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
