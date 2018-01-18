package base.app.data.wall;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class Betting extends BaseItem {

    @JsonProperty("betName")
    private String betName;
    @JsonProperty("odds")
    private Float odds;
    @JsonProperty("outcome")
    private String outcome;
    @JsonProperty("percentage")
    private Float percentage;

    @JsonProperty("betName")
    public String getBetName() {
        return betName;
    }

    @JsonProperty("betName")
    public void setBetName(String betName) {
        this.betName = betName;
    }

    @JsonProperty("odds")
    public Float getOdds() {
        return odds;
    }

    @JsonProperty("odds")
    public void setOdds(Float odds) {
        this.odds = odds;
    }

    @JsonProperty("outcome")
    public String getOutcome() {
        return outcome;
    }

    @JsonProperty("outcome")
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @JsonProperty("percentage")
    public Float getPercentage() {
        return percentage;
    }

    @JsonProperty("percentage")
    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }
}
