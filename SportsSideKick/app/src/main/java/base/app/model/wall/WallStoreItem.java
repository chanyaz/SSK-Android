package base.app.model.wall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import base.app.model.sharing.SharingManager;

/**
 * Created by Filip on 1/10/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class WallStoreItem extends WallBase {

    @JsonProperty("url")
    private String url;

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public void setEqualTo(WallBase item) {
        super.setEqualTo(item);
        this.url = ((WallStoreItem)item).url;
    }

    @Override
    public SharingManager.ItemType getItemType() {
        return null;
    }
}
