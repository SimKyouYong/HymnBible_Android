package co.kr.sky.hymnbible.obj;

import android.os.Parcel;
import android.os.Parcelable;

public class ChurchObj implements Parcelable{
	public static Parcelable.Creator<ChurchObj> getCreator() {
		return CREATOR;
	}	
	String key_index;
	String church_name;		//이름
	String church_type;		//어디 소속
	String person_name;		//사람이름
	String church_address;	//주소
	String church_number;	//번호
	String church_fax;		//팩스번호
	String church_homepage;	//홈페이지 주소
	String church_body;		//한마디
	String church_img;		//이미지
	String church_img2;		//이미지
	String church_img3;		//이미지
	String church_img4;		//이미지
	String church_img5;		//이미지
	String church_img6;		//이미지
	String church_img7;		//이미지
	String church_img8;		//이미지
	String church_img9;		//이미지
	String church_img10;		//이미지
	String search_index;		//이미지
	String Latitude;		//위도
	String Longitude;		//경도
	
	
	
	public ChurchObj(String key_index, String church_name, String church_type, String person_name,
			String church_address, String church_number, String church_fax, String church_homepage, String church_body,
			String church_img, String church_img2, String church_img3, String church_img4, String church_img5,
			String church_img6, String church_img7, String church_img8, String church_img9, String church_img10,
			String search_index , String Latitude , String Longitude) {
		super();
		this.key_index = key_index;
		this.church_name = church_name;
		this.church_type = church_type;
		this.person_name = person_name;
		this.church_address = church_address;
		this.church_number = church_number;
		this.church_fax = church_fax;
		this.church_homepage = church_homepage;
		this.church_body = church_body;
		this.church_img = church_img;
		this.church_img2 = church_img2;
		this.church_img3 = church_img3;
		this.church_img4 = church_img4;
		this.church_img5 = church_img5;
		this.church_img6 = church_img6;
		this.church_img7 = church_img7;
		this.church_img8 = church_img8;
		this.church_img9 = church_img9;
		this.church_img10 = church_img10;
		this.search_index = search_index;
		this.Latitude = Latitude;
		this.Longitude = Longitude;
	}
	
	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getKey_index() {
		return key_index;
	}
	public void setKey_index(String key_index) {
		this.key_index = key_index;
	}
	public String getChurch_name() {
		return church_name;
	}
	public void setChurch_name(String church_name) {
		this.church_name = church_name;
	}
	public String getChurch_type() {
		return church_type;
	}
	public void setChurch_type(String church_type) {
		this.church_type = church_type;
	}
	public String getPerson_name() {
		return person_name;
	}
	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}
	public String getChurch_address() {
		return church_address;
	}
	public void setChurch_address(String church_address) {
		this.church_address = church_address;
	}
	public String getChurch_number() {
		return church_number;
	}
	public void setChurch_number(String church_number) {
		this.church_number = church_number;
	}
	public String getChurch_fax() {
		return church_fax;
	}
	public void setChurch_fax(String church_fax) {
		this.church_fax = church_fax;
	}
	public String getChurch_homepage() {
		return church_homepage;
	}
	public void setChurch_homepage(String church_homepage) {
		this.church_homepage = church_homepage;
	}
	public String getChurch_body() {
		return church_body;
	}
	public void setChurch_body(String church_body) {
		this.church_body = church_body;
	}
	public String getChurch_img() {
		return church_img;
	}
	public void setChurch_img(String church_img) {
		this.church_img = church_img;
	}
	public String getChurch_img2() {
		return church_img2;
	}
	public void setChurch_img2(String church_img2) {
		this.church_img2 = church_img2;
	}
	public String getChurch_img3() {
		return church_img3;
	}
	public void setChurch_img3(String church_img3) {
		this.church_img3 = church_img3;
	}
	public String getChurch_img4() {
		return church_img4;
	}
	public void setChurch_img4(String church_img4) {
		this.church_img4 = church_img4;
	}
	public String getChurch_img5() {
		return church_img5;
	}
	public void setChurch_img5(String church_img5) {
		this.church_img5 = church_img5;
	}
	public String getChurch_img6() {
		return church_img6;
	}
	public void setChurch_img6(String church_img6) {
		this.church_img6 = church_img6;
	}
	public String getChurch_img7() {
		return church_img7;
	}
	public void setChurch_img7(String church_img7) {
		this.church_img7 = church_img7;
	}
	public String getChurch_img8() {
		return church_img8;
	}
	public void setChurch_img8(String church_img8) {
		this.church_img8 = church_img8;
	}
	public String getChurch_img9() {
		return church_img9;
	}
	public void setChurch_img9(String church_img9) {
		this.church_img9 = church_img9;
	}
	public String getChurch_img10() {
		return church_img10;
	}
	public void setChurch_img10(String church_img10) {
		this.church_img10 = church_img10;
	}
	public String getSearch_index() {
		return search_index;
	}
	public void setSearch_index(String search_index) {
		this.search_index = search_index;
	}
	public ChurchObj(Parcel in) {
		readFromParcel(in);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(key_index);
		dest.writeString(church_name);
		dest.writeString(church_type);
		dest.writeString(person_name);
		dest.writeString(church_address);
		dest.writeString(church_number);
		dest.writeString(church_fax);
		dest.writeString(church_homepage);
		dest.writeString(church_body);
		dest.writeString(church_img);
		dest.writeString(church_img2);
		dest.writeString(church_img3);
		dest.writeString(church_img4);
		dest.writeString(church_img5);
		dest.writeString(church_img6);
		dest.writeString(church_img7);
		dest.writeString(church_img8);
		dest.writeString(church_img9);
		dest.writeString(church_img10);
		dest.writeString(search_index);

	}
	private void readFromParcel(Parcel in){
		key_index = in.readString();
		church_name = in.readString();
		church_type = in.readString();
		person_name = in.readString();
		church_address = in.readString();
		church_number = in.readString();
		church_fax = in.readString();
		church_homepage = in.readString();
		church_body = in.readString();
		church_img = in.readString();
		
		church_img2 = in.readString();
		church_img3 = in.readString();
		church_img4 = in.readString();
		church_img5 = in.readString();
		church_img6 = in.readString();
		church_img7 = in.readString();
		church_img8 = in.readString();
		church_img9 = in.readString();
		church_img10 = in.readString();
		search_index = in.readString();

	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<ChurchObj> CREATOR = new Parcelable.Creator() {
		public Object createFromParcel(Parcel in) {
			return new ChurchObj(in);
		}

		public Object[] newArray(int size) {
			return new ChurchObj[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
