package charlychips.com.matrixled.Models

/**
 * Created by NubekDev on 27/07/18.
 */
class Dibujo{
    companion object {
        val TEXTO = 0
        val DIBUJO = 1
    }

    var id:Int? = null
    var tipo:Int? = null
    var nombre:String? = null
    var descripcion:String? = null

    constructor(id:Int,nombre:String,descripcion:String,tipo:Int){
        this.id = id
        this.descripcion = descripcion
        this.nombre = nombre
        this.tipo = tipo
    }
    constructor(nombre:String,descripcion:String,tipo:Int){
        this.descripcion = descripcion
        this.nombre = nombre
        this.tipo = tipo
    }


}