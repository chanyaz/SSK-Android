package base.app.data.wall

data class Pin(
        val sharedComment: String = "",
        val referencedItemClub: String? = null,
        val referencedItemId: String? = null
) : BaseItem()
