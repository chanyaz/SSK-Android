package base.app.data.wall

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

data class News(
        val url: String?,
        val vidUrl: String?
) : WallItem()