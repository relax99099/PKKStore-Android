package pkg.pkkstore.bg

import java.io.Closeable

interface AbstractInstance : Closeable {

    fun launch()

}