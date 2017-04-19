package tv.sportssidekick.sportssidekick.model.sharing;

public interface Shareable {

    void incrementShareCount(SharingManager.ShareTarget shareTarget);

    SharingManager.ItemType getItemType();

}
