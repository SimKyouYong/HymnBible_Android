package co.kr.sky.hymnbible.obj;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MyPhoneListObj2 implements Parcelable{
	public static Parcelable.Creator<MyPhoneListObj2> getCreator() {
		return CREATOR;
	}	
	int position;
	String NAME;
	String PHONE;
	int CHECK;
	
	public MyPhoneListObj2(int position, String nAME, String pHONE, int cHECK) {
		super();
		this.position = position;
		NAME = nAME;
		PHONE = pHONE;
		CHECK = cHECK;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
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
	public int getCHECK() {
		return CHECK;
	}
	public void setCHECK(int cHECK) {
		CHECK = cHECK;
	}
	public MyPhoneListObj2(Parcel in) {
		readFromParcel(in);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(NAME);
		dest.writeString(PHONE);
		dest.writeInt(CHECK);
		dest.writeInt(position);

	}
	private void readFromParcel(Parcel in){
		NAME = in.readString();
		PHONE = in.readString();
		CHECK = in.readInt();
		position = in.readInt();
		
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<MyPhoneListObj2> CREATOR = new Parcelable.Creator() {
		public Object createFromParcel(Parcel in) {
			return new MyPhoneListObj2(in);
		}

		public Object[] newArray(int size) {
			return new MyPhoneListObj2[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
