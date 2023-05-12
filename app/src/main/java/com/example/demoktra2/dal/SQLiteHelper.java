package com.example.demoktra2.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.demoktra2.model.Item;
import com.example.demoktra2.model.ItemTK;
import com.example.demoktra2.model.People;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "test2.db";
    private static int DATABASE_VERSION = 1;
    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateDB = "CREATE TABLE items("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "title TEXT,"+
                "title2 TEXT,"+
                "date TEXT,"+
                "category TEXT,"+
                "category2 TEXT)";
        db.execSQL(sqlCreateDB);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    //get all order by date desc
    public List<Item> getAll(){
        List<Item> list = new ArrayList<>();
        SQLiteDatabase st = getReadableDatabase();
        String order = "date DESC";
        Cursor rs = st.query ("items", null,null,null,null,null,order);
        while(rs!=null && rs.moveToNext()){
            int id =rs.getInt(0);
            String title = rs.getString(1);
            String title2 = rs.getString(2);
            String date = rs.getString(3);
            String c = rs.getString(4);
            String c2 = rs.getString(5);
            list.add(new Item(id,title,title2,date,c,c2));
        }
        return list;
    }

    public List<ItemTK> thongKe(){
        List<ItemTK> list = new ArrayList<>();
        SQLiteDatabase st = getReadableDatabase();
        String sql = "SELECT category, COUNT(category) AS counter FROM items GROUP BY category ORDER BY counter DESC";
        Cursor rs = st.rawQuery(sql,null);
        while(rs!=null && rs.moveToNext()){
            ItemTK itemTK = new ItemTK(rs.getString(0), rs.getInt(1));
            list.add(itemTK);
        }
        return list;
    }
    // add
//    public void addItem(Item i){
//        String sql = "INSERT INTO items(title,category,price,date)"+
//                "VALUES(?,?,?,?)";
//        String[] args = {i.getTitle(), i.getCategory(), i.getPrice(), i.getDate()};
//        SQLiteDatabase st = getWritableDatabase();
//        st.execSQL(sql,args);
//    }
    public long addItem(Item i){
        ContentValues values = new ContentValues();
        values.put("title", i.getTitle());
        values.put("title2", i.getTitle2());
        values.put("date", i.getDate());
        values.put("category", i.getCategory());
        values.put("category2", i.getCategory2());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert("items",null, values);
    }
    // lay item theo date
    public List<Item> getByDate(String date){
        List<Item> list = new ArrayList<>();
        String whereClause = "date like ?";
        String[] whereArgs = {date};
        SQLiteDatabase st =getReadableDatabase();
        Cursor rs = st.query("items",null,whereClause,whereArgs,null,null,null);
        while (rs!=null && rs.moveToNext() ){
            int id =rs.getInt(0);
            String title = rs.getString(1);
            String title2 = rs.getString(2);
            String c = rs.getString(3);
            String c2 = rs.getString(4);
            list.add(new Item(id,title,title2,date,c,c2));
        }

        return list;
    }
    //update
    public int update(Item i){
        ContentValues values = new ContentValues();
        values.put("title", i.getTitle());
        values.put("title2", i.getTitle2());
        values.put("date", i.getDate());
        values.put("category", i.getCategory());
        values.put("category2", i.getCategory2());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String whereClause = "id=?";
        String[] whereArgs = {Integer.toString(i.getId())};
        return sqLiteDatabase.update("items",values, whereClause,whereArgs);

    }
    //delete
    public int delete(int id){
        String whereClause = "id=?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.delete("items",whereClause,whereArgs);
    }

    //search
    public List<Item> searchByTitle(String key){
        List<Item> list = new ArrayList<>();
        String whereClause = "title like ?";
        String[] whereArgs = {"%"+key+"%"};
        SQLiteDatabase st =getReadableDatabase();
        String order = "date";
        Cursor rs = st.query("items",null,whereClause,whereArgs,null,null,order);
        while (rs!=null && rs.moveToNext() ){
            int id =rs.getInt(0);
            String title = rs.getString(1);
            String title2 = rs.getString(2);
            String d = rs.getString(3);
            String c = rs.getString(4);
            String c2 = rs.getString(5);
            list.add(new Item(id,title,title2,d,c,c2));
        }

        return list;
    }
    public List<Item> searchByDes(String key){
        List<Item> list2 = new ArrayList<>();
        String whereClause = "title2 like ?";
        String[] whereArgs = {"%"+key+"%"};
        SQLiteDatabase st =getReadableDatabase();
        String order = "date";
        Cursor rs = st.query("items",null,whereClause,whereArgs,null,null,order);
        while (rs!=null && rs.moveToNext() ){
            int id =rs.getInt(0);
            String title = rs.getString(1);
            String title2 = rs.getString(2);
            String d = rs.getString(3);
            String c = rs.getString(4);
            String c2 = rs.getString(5);
            list2.add(new Item(id,title,title2,d,c,c2));
        }

        return list2;
    }
    public List<Item> searchByCategory(String category){
        List<Item> list = new ArrayList<>();
        String whereClause = "category like ?";
        String[] whereArgs = {category};
        SQLiteDatabase st =getReadableDatabase();
        Cursor rs = st.query("items",null,whereClause,whereArgs,null,null,null);
        while (rs!=null && rs.moveToNext() ){
            int id =rs.getInt(0);
            String title = rs.getString(1);
            String title2 = rs.getString(2);
            String d = rs.getString(3);
            String c = rs.getString(4);
            String c2 = rs.getString(5);
            list.add(new Item(id,title,title2,d,c,c2));
        }

        return list;
    }
    public List<Item> searchByDateFromTo(String from, String to){
        List<Item> list = new ArrayList<>();
        String whereClause = "date BETWEEN ? AND ?";
        String[] whereArgs = {from.trim(),to.trim()};
        SQLiteDatabase st =getReadableDatabase();
        Cursor rs = st.query("items",null,whereClause,whereArgs,null,null,null);
        while (rs!=null && rs.moveToNext() ){
            int id =rs.getInt(0);
            String title = rs.getString(1);
            String title2 = rs.getString(2);
            String d = rs.getString(3);
            String c = rs.getString(4);
            String c2 = rs.getString(5);
            list.add(new Item(id,title,title2,d,c,c2));
        }

        return list;
    }
}
