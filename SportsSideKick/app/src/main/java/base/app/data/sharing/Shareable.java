package base.app.data.sharing;

public interface Shareable {

    void incrementShareCount(SharingManager.ShareTarget shareTarget);

    SharingManager.ItemType getItemType();

}
