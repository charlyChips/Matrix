package charlychips.com.matrixled.Controllers

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.Toast
import charlychips.com.matrixled.Models.Dibujo
import charlychips.com.matrixled.R
import charlychips.com.matrixled.Utils.Custom
import charlychips.com.matrixled.Utils.Sql
import kotlinx.android.synthetic.main.activity_text.*
import kotlinx.android.synthetic.main.dialog_save_dibujo.*

class TextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
        supportActionBar?.hide()

        et_msj.setTypeface(Custom.getTypefaceMatrix(this))

        setClicks()
    }

    fun setClicks(){
        bt_msj_emoji.setOnClickListener({
            val sql = Sql(this)
            val dibujos = sql.getDibujos(Dibujo.EMOJI)

            val b = AlertDialog.Builder(this)
            b.setTitle("Selecciona un Dibujo")

            var items = Array<CharSequence>(dibujos.size+1,{init->""})
            items[0] = "-Crear Emoji-"
            for(i in 0..dibujos.size-1){
                items[i+1] = dibujos[i].nombre!!
            }
            b.setItems(items, DialogInterface.OnClickListener({
                dialog:DialogInterface, which:Int ->

                if(which == 0){
                    val i = Intent(this,EmojiActivity::class.java)
                    startActivity(i)
                    dialog.dismiss()
                    return@OnClickListener
                }

                et_msj.append("@{")
                et_msj.append(dibujos[which-1].descripcion!!)
                et_msj.append("}")


                dialog.dismiss()

            }))
            b.create().show()

        })

        bt_text_save.setOnClickListener({
            val d = Dialog(this)
            d.setContentView(R.layout.dialog_save_dibujo)
            d.window.setLayout(-1,-2)
            val bt = d.findViewById<Button>(R.id.bt_dialogSaveDraw)
            bt.setOnClickListener({
                val et = d.et_dialogSaveDraw
                if(et.text.isNotEmpty()){
                    val sql = Sql(this)
                    val draw = Dibujo(et.text.toString(),et_msj.text.toString(),Dibujo.TEXTO)
                    if (sql.insert(draw)) {
                        d.dismiss()
                    }else{
                        Toast.makeText(this,"Ese nombre ya existe. Pruebe con otro, por favor.", Toast.LENGTH_SHORT).show()
                    }

                }
            })
            d.show()
        })
        bt_text_open.setOnClickListener({
            val sql = Sql(this)
            val dibujos = sql.getDibujos(Dibujo.TEXTO)

            val b = AlertDialog.Builder(this)
            b.setTitle("Selecciona un Mensaje")

            var items = Array<CharSequence>(dibujos.size,{init->""})
            for(i in 0..dibujos.size-1){
                items[i] = dibujos[i].nombre!!
            }
            b.setItems(items, DialogInterface.OnClickListener({
                dialog:DialogInterface, which:Int ->
                et_msj.setText(dibujos[which].descripcion!!)
                dialog.dismiss()

            }))
            b.create().show()
        })
    }
}
