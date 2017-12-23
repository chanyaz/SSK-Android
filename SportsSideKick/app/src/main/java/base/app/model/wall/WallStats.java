package base.app.model.wall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import base.app.model.sharing.SharingManager;

/**
 * Created by Filip on 1/10/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class WallStats extends WallBase {

    private String statName;
    private String subText;
    private float value1;
    private float value2;
    private StatsDisplayType displayType  = StatsDisplayType.number;

    public WallStats(){
        super();
    }

    public void setEqualTo(WallBase item){
        if(item instanceof WallStats){
            WallStats stats = (WallStats) item;
            this.statName = stats.getStatName();
            this.subText = stats.getSubText();
            this.value1 = stats.getValue1();
            this.value2 = stats.getValue2();
            super.setEqualTo(item);
        }
    }

    public String getStatName() {
        return statName;
    }

    public WallStats setStatName(String statName) {
        this.statName = statName;
        return this;
    }

    public String getSubText() {
        return subText;
    }

    public WallStats setSubText(String subText) {
        this.subText = subText;
        return this;
    }

    public float getValue1() {
        return value1;
    }

    public WallStats setValue1(float value1) {
        this.value1 = value1;
        return this;
    }

    public float getValue2() {
        return value2;
    }

    public WallStats setValue2(float value2) {
        this.value2 = value2;
        return this;
    }

    public StatsDisplayType getDisplayType() {
        return displayType;
    }

    public WallStats setDisplayType(StatsDisplayType displayType) {
        this.displayType = displayType;
        return this;
    }

    @Override
    public SharingManager.ItemType getItemType() {
        return null;
    }

    public enum StatsDisplayType{
        percentage,
        number,
        ratio
    }
}
