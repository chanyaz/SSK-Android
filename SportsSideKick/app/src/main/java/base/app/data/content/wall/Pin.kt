package base.app.data.content.wall

data class Pin(
        var sharedComment: String = "",
        val referencedItemClub: String? = null,
        val referencedItemId: String? = null
) : Post()
