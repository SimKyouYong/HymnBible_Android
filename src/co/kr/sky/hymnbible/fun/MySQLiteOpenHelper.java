package co.kr.sky.hymnbible.fun;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	static String NAME = "testdb.sqlite";		//sqlite �̸� ���� 
	static CursorFactory FACTORY = null;		//Ŀ�� �ʱ�ȭ 
	static String PACKEGE = "co.kr.sky.hymnbible";	//��Ű�� �̸� ���� 
	static String DB = "bible_kr.db";			//Į�θ� ��� �̸� ���� 
	static String DB2 = "bible_category.db";			//Į�θ� ��� �̸� ���� 
	static int VERSION  = 1;					//��� ���� 
	public MySQLiteOpenHelper(Context context) {
		super(context, NAME, FACTORY, VERSION);
		// TODO Auto-generated constructor stub
		try {
			boolean bResult = isCheckDB(context);  // DB�� �ִ���?
			Log.i("MiniApp", "DB Check="+bResult);
			if(!bResult){   // DB�� �մ��� Ȯ�� 
				copyDB(context);	
				copyDB2(context);
			}else{

			}
		} catch (Exception e) {

		}
	}
	// DB�� �ֳ� üũ�ϱ�
	public boolean isCheckDB(Context mContext){
		String filePath = "/data/data/" + PACKEGE + "/databases/" + DB;
		File file = new File(filePath);
		///data/data/co.kr.sky.hymnbible/databases/bible_kr.db

		if (file.exists()) {
			return true;
		}

		return false;

	}
	public void copyDB2(Context mContext){
		Log.d("MiniApp", "copyDB2");
		AssetManager manager = mContext.getAssets();
		String folderPath = "/data/data/" + PACKEGE + "/databases";
		String filePath = "/data/data/" + PACKEGE + "/databases/" +DB2;
		File folder = new File(folderPath);
		File file = new File(filePath);

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			InputStream is = manager.open("db/" + DB2);
			BufferedInputStream bis = new BufferedInputStream(is);

			if (folder.exists()) {
			}else{
				folder.mkdirs();
			}


			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			int read = -1;
			byte[] buffer = new byte[1024];
			while ((read = bis.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, read);
			}

			bos.flush();

			bos.close();
			fos.close();
			bis.close();
			is.close();

		} catch (IOException e) {
			Log.e("ErrorMessage : ", e.getMessage());
		} 
	}
	public void copyDB(Context mContext){
		Log.d("MiniApp", "copyDB");
		AssetManager manager = mContext.getAssets();
		String folderPath = "/data/data/" + PACKEGE + "/databases";
		String filePath = "/data/data/" + PACKEGE + "/databases/" +DB;
		File folder = new File(folderPath);
		File file = new File(filePath);

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			InputStream is = manager.open("db/" + DB);
			BufferedInputStream bis = new BufferedInputStream(is);
			if (folder.exists()) {
			}else{
				folder.mkdirs();
			}
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			int read = -1;
			byte[] buffer = new byte[1024];
			while ((read = bis.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, read);
			}
			bos.flush();
			bos.close();
			fos.close();
			bis.close();
			is.close();

		} catch (IOException e) {
			Log.e("ErrorMessage : ", e.getMessage());
		} 
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(SQLiteDatabase db) {
//		String QUERY = "CREATE TABLE word (_id INTEGER PRIMARY KEY autoincrement, word_e TEXT , word_k TEXT)";
//		db.execSQL(QUERY);
		Log.e("ehsk", "eee");

		//        String QUERY1 = "INSERT INTO word (word_e, word_k ) VALUES(apple , ���)";
		//        db.execSQL(QUERY1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String QUERY = "DROP TABLE IF EXISTS word";
		db.execSQL(QUERY);
		onCreate(db);


	}
}