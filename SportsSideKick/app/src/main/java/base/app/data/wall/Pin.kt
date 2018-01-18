package base.app.data.wall

data class Pin(
        var sharedComment: String = "",
        val referencedItemClub: String? = null,
        val referencedItemId: String? = null
) : Post()
