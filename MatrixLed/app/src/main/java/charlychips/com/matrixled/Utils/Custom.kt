package charlychips.com.matrixled.Utils

import android.content.Context
import android.graphics.Typeface

/**
 * Created by NubekDev on 30/07/18.
 */


class Custom{
    companion object {
        fun getTypefaceMatrix(context:Context):Typeface{
            return Typeface.createFromAsset(context.assets,"fonts/matrix.ttf")
        }
    }
}