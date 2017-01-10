package tv.sportssidekick.sportssidekick.model.wall;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallBetting extends WallBase {

    String betName;
    Float odds;
    String outcome;

    public WallBetting(){
        super();
    }

    public Float getPercentage() {
        return percentage;
    }

    public WallBetting setPercentage(Float percentage) {
        this.percentage = percentage;
        return this;
    }

    public String getOutcome() {
        return outcome;
    }

    public WallBetting setOutcome(String outcome) {
        this.outcome = outcome;
        return this;
    }

    public Float getOdds() {
        return odds;
    }

    public WallBetting setOdds(Float odds) {
        this.odds = odds;
        return this;
    }

    public String getBetName() {
        return betName;
    }

    public WallBetting setBetName(String betName) {
        this.betName = betName;
        return this;
    }

    Float percentage;

    public void setEqualTo(WallBase item){
        if (item instanceof WallBetting) {
            WallBetting bet = (WallBetting) item;
            this.betName = bet.getBetName();
            this.odds = bet.getOdds();
            this.outcome = bet.getOutcome();
            this.percentage = bet.getPercentage();
            super.setEqualTo(item);
        }
    }
}
