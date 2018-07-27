package charlychips.com.matrixled.Utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by NubekDev on 27/07/18.
 */
 class Sql: SQLiteOpenHelper{

    constructor(context: Context) : super(context,"SqlMatrixLed",null,1) {

    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS dibujos(id integer primary key, nombre varchar(100),descripcion text, tipo integer)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
    }



}