package com.example.zzha1.lab7_zzha1;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    //file name
    public static final String fileName = "studentInfo";

    public static class MyDataEntry implements BaseColumns {
        //SQL table name
        public static final String TABLE_NAME = "student_grades";

        //SQL column/field names
        public static final String STUDENT_ID_FIELD = "student_id";

        public static final String STUDENT_GRADE_FIELD = "student_grade";
    }

    public class MyDbHelper extends SQLiteOpenHelper{
        //database name
        public static final String DB_NAME = "StudentInfo.db";

        //database version
        public static final int DB_VERSION = 1;

        public static final String table_query = "CREATE TABLE " + MyDataEntry.TABLE_NAME + " (" +
                MyDataEntry._ID + " INTEGER PRIMARY KEY," + MyDataEntry.STUDENT_ID_FIELD + " TEXT," +
                MyDataEntry.STUDENT_GRADE_FIELD + " TEXT )";

        public static final String delete_query = "DROP TABLE IF EXISTS " + MyDataEntry.TABLE_NAME;

        public MyDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("Debug", "onCreate table query called here!");

            db.execSQL(table_query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("Debug", "onUpgrade table query called here!");

            db.execSQL(delete_query);
        }
    }

    protected void loadSharedPreferences(){
        Log.i("Debug", "loadSharedPreferences called here!");

        SharedPreferences sharedPreferences = getSharedPreferences(fileName, 0);
        long studentId = sharedPreferences.getLong("studentID", -1);
        if(studentId > 0){
            EditText studentID = (EditText) findViewById(R.id.studentId);
            studentID.setText(""+studentId);
        }
        //TODO: Load grade here as well.
    }

    protected void saveSharedPreferences(){
        Log.i("Debug", "saveSharedPreferences called here!");

        SharedPreferences sharedPreferences = getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        EditText studentID = (EditText) findViewById(R.id.studentId);
        long studentId = Long.parseLong(studentID.getText().toString());
        editor.putLong("studentID", studentId);
        //TODO: Save grade here as well.

        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load preferences
        loadSharedPreferences();

        //Add data to database
        //TODO: also add to ListView as well.
        Button saveGradeButton = (Button) findViewById(R.id.button);
        saveGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGrade();
            }
        });

        loadDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //save preferences
        saveSharedPreferences();

        saveGrade();
    }

    protected void saveGrade(){
        //TODO: put this in an AsyncTask
        MyDbHelper herpler = new MyDbHelper(this);
        SQLiteDatabase db = herpler.getReadableDatabase();
        ContentValues newRow = new ContentValues();
        EditText studentID = (EditText) findViewById(R.id.studentId);
        long studentId = Long.parseLong(studentID.getText().toString());
        EditText grade = (EditText) findViewById(R.id.grade);
        String studentGrade = grade.getText().toString();
        newRow.put(MyDataEntry.STUDENT_ID_FIELD, studentId);
        newRow.put(MyDataEntry.STUDENT_GRADE_FIELD, studentGrade);

        //the middle argument (null) is what to insert in case newRow is itself a null object
        //returns the primary key value for the new row if it was successful
        long newRowId = db.insert(MyDataEntry.TABLE_NAME, null, newRow);

        Log.i("Degub", "result of database insertion: " + newRowId);
    }

    protected void loadDatabase(){
        MyDbHelper helper = new MyDbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[]  query_columns = {
                MyDataEntry._ID,
                MyDataEntry.STUDENT_ID_FIELD,
                MyDataEntry.STUDENT_GRADE_FIELD
        };
        String selectQuery = MyDataEntry.STUDENT_ID_FIELD + " = ?";
        String[] selectionArgs = {" Filter string "};
        String sortOrder = MyDataEntry.STUDENT_ID_FIELD + " DESC";
        Cursor cursor = db.query(
                MyDataEntry.TABLE_NAME,
                query_columns,
                null,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        boolean hasMoreData = cursor.moveToFirst();
        while(hasMoreData){
            long key = cursor.getLong(cursor.getColumnIndexOrThrow(MyDataEntry._ID));
            String studentId = cursor.getString(cursor.getColumnIndexOrThrow(MyDataEntry.STUDENT_ID_FIELD));
            String studentGrade = cursor.getString(cursor.getColumnIndexOrThrow(MyDataEntry.STUDENT_GRADE_FIELD));

            System.out.println("Record Key: " + key + " student id: " +studentId + " student grade: " + studentGrade);

            //TODO: populate an ArrayList that backs a ListView.
        }
    }
}
