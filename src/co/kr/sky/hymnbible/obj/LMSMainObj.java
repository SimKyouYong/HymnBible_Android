package co.kr.sky.hymnbible.obj;

import android.os.Parcel;
import android.os.Parcelable;

public class LMSMainObj implements Parcelable{
	public static Parcelable.Creator<LMSMainObj> getCreator() {
		return CREATOR;
	}	
	String name;
	String number;
	
	
	public LMSMainObj(String name, String number) {
		super();
		this.name = name;
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public LMSMainObj(Parcel in) {
		readFromParcel(in);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(number);

	}
	private void readFromParcel(Parcel in){
		name = in.readString();
		number = in.readString();

	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<LMSMainObj> CREATOR = new Parcelable.Creator() {
		public Object createFromParcel(Parcel in) {
			return new LMSMainObj(in);
		}

		public Object[] newArray(int size) {
			return new LMSMainObj[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
