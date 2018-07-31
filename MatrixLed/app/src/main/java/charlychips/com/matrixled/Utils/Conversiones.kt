package charlychips.com.matrixled.Utils

import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import charlychips.com.matrixled.Models.Dot

/**
 * Created by NubekDev on 31/07/18.
 */

class Conversiones{
    companion object {

        fun getBinFromDots(dots:ArrayList<Dot>):String{
            var pointer = 1
            var binario = StringBuilder()
            for(dot in dots!!){

                if(dot.on) binario.append("1") else binario.append("0")
                if(pointer % 4 == 0) binario.append("-") else binario.append("")
                //s += if((pointer % 32) == 0) "\r\n" else ""

                pointer++
            }
            return binario.toString()
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



        fun drawFromHexString(activity: AppCompatActivity,dots:ArrayList<Dot>, hex:String, canvas:Canvas, frame: View){
            val bin = Conversiones.convertToBinString(hex)
            val ca = bin.toCharArray()

            if(ca.size != dots!!.size){
                Log.d("DrawFromHex","Los arreglos son de diferente longitud")
                return
            }

            for(i in 0..ca.size-1){
                dots!![i].on = if(ca[i]=='1') true else false
                val pincel = if(dots!![i].on) Pinceles.getPincelLedOn(activity) else Pinceles.getPincelLedOff(activity)
                dots!![i].drawInCanvas(canvas!!,pincel,frame)
            }

        }
    }
}