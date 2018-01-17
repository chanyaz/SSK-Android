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
public class Stats extends WallItem {

    private String statName;
    private String subText;
    private float value1;
    private float value2;
    private StatsDisplayType displayType  = StatsDisplayType.number;

    public Stats(){
        super();
    }

    public String getStatName() {
        return statName;
    }

    public Stats setStatName(String statName) {
        this.statName = statName;
        return this;
    }

    public String getSubText() {
        return subText;
    }

    public Stats setSubText(String subText) {
        this.subText = subText;
        return this;
    }

    public float getValue1() {
        return value1;
    }

    public Stats setValue1(float value1) {
        this.value1 = value1;
        return this;
    }

    public float getValue2() {
        return value2;
    }

    public Stats setValue2(float value2) {
        this.value2 = value2;
        return this;
    }

    public StatsDisplayType getDisplayType() {
        return displayType;
    }

    public Stats setDisplayType(StatsDisplayType displayType) {
        this.displayType = displayType;
        return this;
    }

    public enum StatsDisplayType{
        percentage,
        number,
        ratio
    }
}
