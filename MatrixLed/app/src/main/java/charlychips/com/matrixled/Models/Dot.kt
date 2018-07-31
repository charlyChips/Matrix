package charlychips.com.matrixled.Models

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

/**
 * Created by NubekDev on 31/07/18.
 */
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