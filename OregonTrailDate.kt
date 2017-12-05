import java.io.Serializable

class OregonTrailDate constructor(private var year: Int = 1847, private var month: Int = 1, private var day: Int = 1) : Serializable{
    override fun toString(): String{
        val monthString = when(month){
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            else -> "December"
        }
        val dayString = when(day){
            0, 20, 30 -> "${day + 1}st"
            1, 21 -> "${day + 1}nd"
            2, 22 -> "${day + 1}rd"
            else -> "${day + 1}th"
        }
        return "$dayString of $monthString, $year"
    }

    fun advance(days: Int){
        day += days
        month += day / daysInMonth()
        year += month / 12
        day %= daysInMonth()
        month %= 12
    }

    private fun daysInMonth(): Int{
        return when (month) {
            2 -> 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }
}
