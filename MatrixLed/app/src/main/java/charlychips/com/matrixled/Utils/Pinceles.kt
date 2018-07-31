package charlychips.com.matrixled.Utils

import android.graphics.Paint
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import charlychips.com.matrixled.R

/**
 * Created by NubekDev on 31/07/18.
 */
class Pinceles{
    companion object {
        fun getMyColor(activity:AppCompatActivity,id:Int):Int{
            return ResourcesCompat.getColor(activity.resources,id,null)
        }
        fun getPincelLedOff(activity:AppCompatActivity): Paint {
            val pincel = Paint()
            pincel.color = getMyColor(activity,R.color.colorLedOff)
            pincel.strokeWidth = 2f
            return pincel
        }
        fun getPincelLedOn(activity:AppCompatActivity): Paint {
            val pincel = Paint()
            pincel.color = getMyColor(activity,R.color.colorLedOn)
            pincel.strokeWidth = 2f
            return pincel
        }
    }
}