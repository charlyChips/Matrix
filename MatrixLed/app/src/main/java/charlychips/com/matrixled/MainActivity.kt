package charlychips.com.matrixled

import android.app.ActionBar
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.CharSequenceTransformation
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.text.Layout
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import charlychips.com.matrixled.Models.Dibujo
import charlychips.com.matrixled.Utils.Custom
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
            val pincelOff = getPincelLedOff()


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
                            val pincel = if(dot.on) getPincelLedOn() else getPincelLedOff()
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
                    dot.drawInCanvas(canvas!!,getPincelLedOff(),frame_canvas)
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
            var pointer = 1
            var binario = StringBuilder()
            for(dot in dots!!){

                if(dot.on) binario.append("1") else binario.append("0")
                if(pointer % 4 == 0) binario.append("-") else binario.append("")
                //s += if((pointer % 32) == 0) "\r\n" else ""

                pointer++
            }

            val hex = convertToHexString(binario.toString())
            val bin = convertToBinString(hex)
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
                drawFromHexString(dibujos[which].descripcion!!)
                dialog.dismiss()
                tv_debug.text = "Cargado Correctamente -${dibujos[which].nombre!!}-"

            }))
            b.create().show()

        })
    }

    //======================================================================================
    //====================== H E L P E R S =================================================
    //======================================================================================
    fun getMyColor(id:Int):Int{
        return ResourcesCompat.getColor(resources,id,null)
    }
    fun getPincelLedOff():Paint{
        val pincel = Paint()
        pincel.color = getMyColor(R.color.colorLedOff)
        pincel.strokeWidth = 2f
        return pincel
    }
    fun getPincelLedOn():Paint{
        val pincel = Paint()
        pincel.color = getMyColor(R.color.colorLedOn)
        pincel.strokeWidth = 2f
        return pincel
    }
    fun convertToHexString(binario:String):String{

        val split = binario.split("-".toRegex())
        var sb = StringBuilder()
        for(s in split){
            val ca = s.toCharArray()
            var value:Int = 0

            if(ca.size == 4) {
                value += if (ca[0] == '1') 8 else 0
                value += if (ca[1] == '1') 4 else 0
                value += if (ca[2] == '1') 2 else 0
                value += if (ca[3] == '1') 1 else 0

                when (value) {
                    10 -> sb.append('A')
                    11 -> sb.append('B')
                    12 -> sb.append('C')
                    13 -> sb.append('D')
                    14 -> sb.append('E')
                    15 -> sb.append('F')
                    else -> sb.append("${value}")
                }
            }
        }
        return sb.toString()
    }

    fun drawFromHexString(hex:String){
        val bin = convertToBinString(hex)
        val ca = bin.toCharArray()

        if(ca.size != dots!!.size){
            Log.d("DrawFromHex","Los arreglos son de diferente longitud")
            return
        }

        for(i in 0..ca.size-1){
            dots!![i].on = if(ca[i]=='1') true else false
            val pincel = if(dots!![i].on) getPincelLedOn() else getPincelLedOff()
            dots!![i].drawInCanvas(canvas!!,pincel,frame_canvas)
        }

    }
    fun convertToBinString(hex:String):String{
        val ca = hex.toCharArray()
        var sb = StringBuilder()

        for(c in ca){
            when(c){
                '0'->{
                    sb.append("0000")
                }
                '1'->{
                    sb.append("0001")
                }
                '2'->{
                    sb.append("0010")
                }
                '3'->{
                    sb.append("0011")
                }
                '4'->{
                    sb.append("0100")
                }
                '5'->{
                    sb.append("0101")
                }
                '6'->{
                    sb.append("0110")
                }
                '7'->{
                    sb.append("0111")
                }
                '8'->{
                    sb.append("1000")
                }
                '9'->{
                    sb.append("1001")
                }
                'A'->{
                    sb.append("1010")
                }
                'B'->{
                    sb.append("1011")
                }
                'C'->{
                    sb.append("1100")
                }
                'D'->{
                    sb.append("1101")
                }
                'E'->{
                    sb.append("1110")
                }
                'F'->{
                    sb.append("1111")
                }

            }
        }

        return sb.toString()
    }



    //======================================================================================
    //====================== C L A S E S ===================================================
    //======================================================================================
    class Coordinates{
        var x:Float
        var y:Float
        constructor(x:Float,y:Float){
            this.x = x
            this.y = y
        }

    }
    class Dot{
        var coordinates:Coordinates
        var on = false
        var width:Float
        var height:Float
        var radius:Float

        constructor(coordinates: Coordinates, width:Float, height:Float, radius:Float){
            this.coordinates = coordinates
            this.width = width
            this.height = height
            this.radius = radius
        }

        fun isNear(coordinates: Coordinates):Boolean{
            var difX = this.coordinates.x - coordinates.x
            var difY = this.coordinates.y - coordinates.y

            if(difX < 0) difX *= -1
            if(difY < 0) difY *= -1

            if(difX <= (radius) && difY <= (radius)){
                on = !on
                return true
            }
            return false

        }
        fun drawInCanvas(canvas: Canvas, pincel : Paint, viewToInvalidate: View){
            canvas!!.drawCircle(coordinates.x, coordinates.y, radius, pincel)
            viewToInvalidate.invalidate()
        }
    }
}
