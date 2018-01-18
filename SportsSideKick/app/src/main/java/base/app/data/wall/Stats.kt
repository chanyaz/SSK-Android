package base.app.data.wall

class Stats : Post() {

    private var statName: String? = null
    private var subText: String? = null
    private var value1: Float = 0.toFloat()
    private var value2: Float = 0.toFloat()

    fun getStatName(): String? {
        return statName
    }

    fun setStatName(statName: String): Stats {
        this.statName = statName
        return this
    }

    fun getSubText(): String? {
        return subText
    }

    fun setSubText(subText: String): Stats {
        this.subText = subText
        return this
    }
}
