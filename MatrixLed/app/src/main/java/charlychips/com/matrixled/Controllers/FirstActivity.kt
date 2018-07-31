package charlychips.com.matrixled.Controllers

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import charlychips.com.matrixled.R
import charlychips.com.matrixled.Utils.Custom

import kotlinx.android.synthetic.main.activity_first.*
import kotlinx.android.synthetic.main.content_first.*

class FirstActivity : AppCompatActivity() {

    var recorrerTask: RecorrerTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        setSupportActionBar(toolbar)

        tv_titulo.setTypeface(Custom.getTypefaceMatrix(this))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


        bt_first_text.setOnClickListener({
            val i = Intent(this, TextActivity::class.java)
            startActivity(i)
        })
        bt_first_dibujo.setOnClickListener({
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        })

        bt_first_emoji.setOnClickListener({
            val i = Intent(this, EmojiActivity::class.java)
            startActivity(i)
        })
    }

    override fun onResume() {
        super.onResume()
        recorrerTask = RecorrerTask(tv_titulo)
        recorrerTask!!.execute()
    }

    override fun onPause() {
        super.onPause()
        recorrerTask?.cancel(true)
    }



    class RecorrerTask:AsyncTask<Integer,Float,Integer>{

        var view:View
        constructor(view: View){
            this.view = view

        }
        override fun doInBackground(vararg p0: Integer?): Integer {
            while (true){
                for(i in 0..160) {
                    try {
                        Thread.sleep(30)
                    }catch(e:Exception){

                    }
                    val progress = 800 - (i * 10)
                    publishProgress(progress.toFloat())
                }
            }
        }

        override fun onProgressUpdate(vararg values: Float?) {
            super.onProgressUpdate(*values)

            view.translationX = values[0]!!

        }

    }

}
