package base.app.data.wall

data class News(
        val url: String? = null,
        val vidUrl: String? = null,
        val source: String? = null
) : WallItem()