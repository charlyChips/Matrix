package charlychips.com.matrixled.Utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import charlychips.com.matrixled.Models.Dibujo

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


    fun insert(dibujo: Dibujo):Boolean{
        val db = writableDatabase

        val cursor = db.rawQuery("SELECT id FROM dibujos WHERE nombre = '${dibujo.nombre}'",null)
        if(cursor.count>0){
            db.close()
            return false
        }

        db.execSQL("INSERT INTO dibujos (nombre,descripcion,tipo) VALUES('${dibujo.nombre!!}','${dibujo.descripcion!!}','${dibujo.tipo!!}')")
        db.close()
        return true
    }

    fun getDibujos(tipo:Int):ArrayList<Dibujo>{
        val db = readableDatabase
        val c = db.rawQuery("SELECT * FROM dibujos WHERE tipo = ${tipo}",null)
        var dibujos = ArrayList<Dibujo>()
        while(c.moveToNext()){
            dibujos.add(Dibujo(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3)))
        }
        db.close()
        return dibujos
    }

}