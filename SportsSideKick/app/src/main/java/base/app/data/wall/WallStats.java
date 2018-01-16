package base.app.data.wall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class WallStats extends WallItem {

    private String statName;
    private String subText;
    private float value1;
    private float value2;
    private StatsDisplayType displayType  = StatsDisplayType.number;

    public WallStats(){
        super();
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

    public enum StatsDisplayType{
        percentage,
        number,
        ratio
    }
}
