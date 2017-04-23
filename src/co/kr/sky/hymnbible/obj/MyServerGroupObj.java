package co.kr.sky.hymnbible.obj;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MyServerGroupObj implements Parcelable{
	public static Parcelable.Creator<MyServerGroupObj> getCreator() {
		return CREATOR;
	}	
	String key_index;
	String name;
	String count;
	String my_phone;
	int check;
	public MyServerGroupObj(String item1, String item2, String item3, String item4 , int check) {
		super();
		this.key_index = item1;
		this.name = item2;
		this.count = item3;
		this.my_phone = item4;
		this.check = check;
	}
	
	
	public int getCheck() {
		return check;
	}


	public void setCheck(int check) {
		this.check = check;
	}




	public String getCount() {
		return count;
	}


	public void setCount(String count) {
		this.count = count;
	}


	public String getMy_phone() {
		return my_phone;
	}


	public void setMy_phone(String my_phone) {
		this.my_phone = my_phone;
	}


	public String getKey_index() {
		return key_index;
	}
	public void setKey_index(String key_index) {
		this.key_index = key_index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MyServerGroupObj(Parcel in) {
		readFromParcel(in);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		
		dest.writeString(key_index);
		dest.writeString(name);
		dest.writeString(count);
		dest.writeString(my_phone);
		dest.writeInt(check);
		
	}
	private void readFromParcel(Parcel in){
		key_index = in.readString();
		name = in.readString();
		count = in.readString();
		my_phone = in.readString();
		check = in.readInt();

	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<MyServerGroupObj> CREATOR = new Parcelable.Creator() {
		public Object createFromParcel(Parcel in) {
			return new MyServerGroupObj(in);
		}

		public Object[] newArray(int size) {
			return new MyServerGroupObj[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
