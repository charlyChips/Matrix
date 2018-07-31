package charlychips.com.matrixled.Controllers

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import charlychips.com.matrixled.Models.Coordinates
import charlychips.com.matrixled.Models.Dibujo
import charlychips.com.matrixled.Models.Dot
import charlychips.com.matrixled.R
import charlychips.com.matrixled.Utils.Conversiones
import charlychips.com.matrixled.Utils.Pinceles
import charlychips.com.matrixled.Utils.Sql
import kotlinx.android.synthetic.main.activity_emoji.*
import kotlinx.android.synthetic.main.dialog_save_dibujo.*

class EmojiActivity : AppCompatActivity() {

    var firstDrawed = false
    var height:Int? = null
    var width:Int? = null
    var dotWidth:Float? = null
    var dotHeight:Float? = null
    var dotRadius:Float? = null
    var canvas:Canvas? = null
    var dots:ArrayList<Dot>? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji)
        supportActionBar?.hide()
        setClicks()
    }





    fun setClicks(){
        canvas_emoji.setOnTouchListener({
            v: View?, e: MotionEvent? ->

            if(e != null){
                if(e!!.action == MotionEvent.ACTION_DOWN || e!!.action == MotionEvent.ACTION_MOVE){
                    val c = Coordinates(e!!.x , e!!.y)
                    Log.d("Image Touch","Touched -> ${e!!.x} & ${e!!.y}")
                    for(dot in dots!!){
                        if(dot.isNear(c)){
                            dot.on = bt_emoji_ledToggle.isChecked
                            val pincel = if(dot.on) Pinceles.getPincelLedOn(this) else Pinceles.getPincelLedOff(this)
                            dot.drawInCanvas(canvas!!,pincel,canvas_emoji)

                        }
                    }
                }
            }
            true
        })

        bt_emoji_delete.setOnClickListener({
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Limpiar Matriz?")
            builder.setMessage("Si confirmas perderás tu dibujo.")
            builder.setPositiveButton("Confirmar", DialogInterface.OnClickListener({
                dialog: DialogInterface, which:Int ->

                for(dot in dots!!){
                    dot.on = false
                    dot.drawInCanvas(canvas!!,Pinceles.getPincelLedOff(this),canvas_emoji)
                }
                dialog.dismiss()

            }))
            builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener({
                dialog: DialogInterface, which:Int ->
                dialog.dismiss()
            }))
            builder.create().show()
        })

        bt_emoji_save.setOnClickListener({
            val binario = Conversiones.getBinFromDots(dots!!)

            val hex = Conversiones.convertToHexString(binario)
            Log.d("Hex",hex)

            val d = Dialog(this)
            d.setContentView(R.layout.dialog_save_dibujo)
            d.window.setLayout(-1,-2)
            val bt = d.findViewById<Button>(R.id.bt_dialogSaveDraw)
            bt.setOnClickListener({
                val et = d.et_dialogSaveDraw
                if(et.text.isNotEmpty()){
                    val sql = Sql(this)
                    val draw = Dibujo(et.text.toString(),hex, Dibujo.EMOJI)
                    if (sql.insert(draw)) {
                        d.dismiss()
                    }else{
                        Toast.makeText(this,"Ese nombre ya existe. Pruebe con otro, por favor.", Toast.LENGTH_SHORT).show()
                    }

                }
            })
            d.show()

        })
        bt_emoji_load.setOnClickListener({
            val sql = Sql(this)
            val dibujos = sql.getDibujos(Dibujo.EMOJI)

            val b = AlertDialog.Builder(this)
            b.setTitle("Selecciona un Dibujo")

            var items = Array<CharSequence>(dibujos.size,{init->""})
            for(i in 0..dibujos.size-1){
                items[i] = dibujos[i].nombre!!
            }
            b.setItems(items, DialogInterface.OnClickListener({
                dialog:DialogInterface, which:Int ->
                Conversiones.drawFromHexString(this,dots!!,dibujos[which].descripcion!!,canvas!!,canvas_emoji)
                dialog.dismiss()

            }))
            b.create().show()

        })

    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)


        Log.d("MainActivity","Focus -> ${hasFocus}")
        if(hasFocus && !firstDrawed) {

            height = canvas_emoji.height
            width = canvas_emoji.width


            dotWidth = (width!!.toFloat()/8)
            dotHeight = (height!!.toFloat()/8)

            dotRadius = if (dotWidth!!<dotHeight!!) dotWidth!!*0.4f else dotHeight!!*0.4f

            val bitmap = Bitmap.createBitmap(width!!,height!!, Bitmap.Config.ARGB_8888)
            canvas_emoji.setImageBitmap(bitmap)
            canvas = Canvas(bitmap)
            val pincelOff = Pinceles.getPincelLedOff(this)


            //canvas!!.drawColor(getMyColor(R.color.colorCanvasBAckground))

            //===========================================================
            //============ Dibujando LEDS ===============================
            //===========================================================

            dots = ArrayList<Dot>()
            val startX = dotWidth!! / 2
            val startY = dotHeight!! / 2
            for(j in 0..7) {
                val y = ((dotWidth!! * j) + startY)
                for (i in 0..7) {
                    val x = (dotWidth!! * i) + startX
                    canvas!!.drawCircle(x, y, dotRadius!!, pincelOff)
                    dots!!.add(Dot(Coordinates(x,y),dotWidth!!,dotHeight!!,dotRadius!!))
                }
            }
            canvas_emoji.invalidate()

            for (dot in dots!!){
                Log.d("Dot Emoji Created","${dot.coordinates.x} , ${dot.coordinates.y}")
            }
            firstDrawed = true


        }
    }

}
