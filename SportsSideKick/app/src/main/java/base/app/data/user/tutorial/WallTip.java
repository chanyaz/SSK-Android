package base.app.data.user.tutorial;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import base.app.data.content.wall.FeedItem;

/**
 * Created by Filip on 3/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallTip extends FeedItem {

    private int tipNumber;
    private String tipText;
    private boolean tipRead;
    private String tipTittle;
    private List<WallStep> tipSteps;
    private String tipDescription;
    private String tipEnding;
    private int tipEarnings;

    public WallTip(int tipNumber, String tipText, String tipTittle, List<WallStep> tipSteps, String tipDescription, String tipEnding, int tipEarnings) {
        this.tipNumber = tipNumber;
        this.tipText = tipText;
        this.tipTittle = tipTittle;
        this.tipSteps = tipSteps;
        this.tipDescription = tipDescription;
        this.tipEnding = tipEnding;
        this.tipEarnings = tipEarnings;
    }

    public int getTipNumber() {
        return tipNumber;
    }

    public void setTipNumber(int tipNumber) {
        this.tipNumber = tipNumber;
    }

    public String getTipText() {
        return tipText;
    }

    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    public boolean isTipRead() {
        return tipRead;
    }

    public void setTipRead(boolean tipRead) {
        this.tipRead = tipRead;
    }

    public String getTipTittle() {
        return tipTittle;
    }

    public void setTipTittle(String tipTittle) {
        this.tipTittle = tipTittle;
    }

    public List<WallStep> getTipSteps() {
        return tipSteps;
    }

    public void setTipSteps(List<WallStep> tipSteps) {
        this.tipSteps = tipSteps;
    }

    public String getTipDescription() {
        return tipDescription;
    }

    public void setTipDescription(String tipDescription) {
        this.tipDescription = tipDescription;
    }

    public String getTipEnding() {
        return tipEnding;
    }

    public void setTipEnding(String tipEnding) {
        this.tipEnding = tipEnding;
    }

    public int getTipEarnings() {
        return tipEarnings;
    }

    public void setTipEarnings(int tipEarnings) {
        this.tipEarnings = tipEarnings;
    }

    public boolean hasBeenSeen(){
       return Prefs.getBoolean("tip_seen_" + tipNumber,false);
    }

    public void markAsSeen(){
        Prefs.putBoolean("tip_seen_" + tipNumber,true);
    }

    public void markAsNotSeen(){
        Prefs.putBoolean("tip_seen_" + tipNumber,false);
    }

}
