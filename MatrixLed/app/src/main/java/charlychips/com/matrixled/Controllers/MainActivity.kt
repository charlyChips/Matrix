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
import charlychips.com.matrixled.Utils.Custom
import charlychips.com.matrixled.Utils.Pinceles
import charlychips.com.matrixled.Utils.Sql
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_save_dibujo.*

class MainActivity : AppCompatActivity() {

    var firstDrawed = false
    var dotRadius:Float? =  null
    var dotWidth:Float? = null
    var dotHeight:Float? = null
    var height:Int? = null
    var width:Int? = null
    var dotsCoordinates:ArrayList<Coordinates>? = null
    var dots : ArrayList<Dot>? = null


    var canvas: Canvas? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        bt_ledToggle.isChecked = true
        setClicks()
        tv_debug.setTypeface(Custom.getTypefaceMatrix(this))
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)


        Log.d("MainActivity","Focus -> ${hasFocus}")
        if(hasFocus && !firstDrawed) {

            height = frame_canvas.height
            width = frame_canvas.width


            dotWidth = (width!!.toFloat()/32)
            dotHeight = (height!!.toFloat()/8)

            dotRadius = if (dotWidth!!<dotHeight!!) dotWidth!!*0.4f else dotHeight!!*0.4f

            val bitmap = Bitmap.createBitmap(width!!,height!!,Bitmap.Config.ARGB_8888)
            frame_canvas.setImageBitmap(bitmap)
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
                for (i in 0..31) {
                    val x = (dotWidth!! * i) + startX
                    canvas!!.drawCircle(x, y, dotRadius!!, pincelOff)
                    dots!!.add(Dot(Coordinates(x,y),dotWidth!!,dotHeight!!,dotRadius!!))
                }
            }
            frame_canvas.invalidate()

            for (dot in dots!!){
                Log.d("Dot Created","${dot.coordinates.x} , ${dot.coordinates.y}")
            }
            firstDrawed = true


        }
    }



    //======================================================================================
    //====================== C L I C K S ===================================================
    //======================================================================================
    fun setClicks(){
        frame_canvas.setOnTouchListener({
            v: View?, e: MotionEvent? ->

            if(e != null){
                if(e!!.action == MotionEvent.ACTION_DOWN || e!!.action == MotionEvent.ACTION_MOVE){
                    val c = Coordinates(e!!.x , e!!.y)
                    Log.d("Image Touch","Touched -> ${e!!.x} & ${e!!.y}")
                    for(dot in dots!!){
                        if(dot.isNear(c)){
                            dot.on = bt_ledToggle.isChecked
                            val pincel = if(dot.on) Pinceles.getPincelLedOn(this) else Pinceles.getPincelLedOff(this)
                            dot.drawInCanvas(canvas!!,pincel,frame_canvas)

                        }
                    }
                }
            }
            true
        })

        bt_limpiar.setOnClickListener({
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Limpiar Matriz?")
            builder.setMessage("Si confirmas perderás tu dibujo.")
            builder.setPositiveButton("Confirmar", DialogInterface.OnClickListener({
                dialog:DialogInterface, which:Int ->

                for(dot in dots!!){
                    dot.on = false
                    dot.drawInCanvas(canvas!!,Pinceles.getPincelLedOff(this),frame_canvas)
                    tv_debug.text = "Nuevo Dibujo"
                }
                dialog.dismiss()

            }))
            builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener({
                dialog:DialogInterface, which:Int ->
                dialog.dismiss()
            }))
            builder.create().show()
        })

        bt_save.setOnClickListener({
            tv_debug.text = ""
            val binario = Conversiones.getBinFromDots(dots!!)

            val hex = Conversiones.convertToHexString(binario)
            val bin = Conversiones.convertToBinString(hex)
            Log.d("Hex",hex)


            val d = Dialog(this)
            d.setContentView(R.layout.dialog_save_dibujo)
            d.window.setLayout(-1,-2)
            val bt = d.findViewById<Button>(R.id.bt_dialogSaveDraw)
            bt.setOnClickListener({
                val et = d.et_dialogSaveDraw
                if(et.text.isNotEmpty()){
                    val sql = Sql(this)
                    val draw = Dibujo(et.text.toString(),hex,Dibujo.DIBUJO)
                    if (sql.insert(draw)) {
                        tv_debug.text = "Guardado Correctamente"
                        d.dismiss()
                    }else{
                        Toast.makeText(this,"Ese nombre ya existe. Pruebe con otro, por favor.",Toast.LENGTH_SHORT).show()
                    }

                }
            })
            d.show()

        })
        bt_load.setOnClickListener({
            val sql = Sql(this)
            val dibujos = sql.getDibujos(Dibujo.DIBUJO)

            val b = AlertDialog.Builder(this)
            b.setTitle("Selecciona un Dibujo")

            var items = Array<CharSequence>(dibujos.size,{init->""})
            for(i in 0..dibujos.size-1){
                items[i] = dibujos[i].nombre!!
            }
            b.setItems(items, DialogInterface.OnClickListener({
                dialog:DialogInterface, which:Int ->
                Conversiones.drawFromHexString(this,dots!!,dibujos[which].descripcion!!,canvas!!,frame_canvas)
                dialog.dismiss()
                tv_debug.text = "Cargado Correctamente -${dibujos[which].nombre!!}-"

            }))
            b.create().show()

        })
    }


}
