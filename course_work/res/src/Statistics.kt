import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.sqrt

class Statistics {


    public fun calculate(xList: List<Double>) {
        val M = calculateExpectedValue(xList)
        val D = calculateDispersion(xList, M)
        val S = calculateDeviation(D)
        val avgS = S / sqrt(xList.size.toDouble())
        val student = 2.13
        val delta : Double = student * avgS

        val MBD = BigDecimal(M).setScale(2, RoundingMode.HALF_EVEN)
        val deltaBD = BigDecimal(delta).setScale(2, RoundingMode.HALF_EVEN)
        print("Результат при \t X : $xList\n" +
                "\tM: $MBD\n" +
                "\tD: $D\n" +
                "\tS: $S\n" +
                "\tavgS : $avgS\n" +
                "\tstudent : $student\n" +
                "\tdelta: $deltaBD\n" +
                "\n" +
                "$MBD +- $deltaBD\n\n")
    }


    private fun calculateExpectedValue(xList : List<Double>) : Double {
        var sum = 0.0
        for (x in xList) sum += x
        return sum / xList.size
    }


    private fun calculateDispersion(xList : List<Double>, M : Double) : Double {
        var sum = 0.0
        for (x in xList) sum += (x - M).pow(2)

        return (1.0 / (xList.size - 1.0)) * sum
    }

    private fun calculateDeviation(D : Double) : Double {
        return sqrt(D)
    }

}