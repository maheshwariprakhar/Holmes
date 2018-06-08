package SqlLite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class NotificationsDbHelper extends SQLiteOpenHelper {

    private Context mContext;
    public static final String CREATE_Table = "create table if not exists Notification (" +
            "notificationid integer primary key autoincrement, " +
            "type integer, Hid text, Hname text, Hostid text, Hostname text, timestamp text);";

    public NotificationsDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(CREATE_Table);
            //db.execSQL(TRUNCATE_TVUser);
            //Toast.makeText(mContext, "Create Table Success", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            //Toast.makeText(mContext, "Create Table Failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
