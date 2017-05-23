package base.app.model.sharing;

public interface Shareable {

    void incrementShareCount(SharingManager.ShareTarget shareTarget);

    SharingManager.ItemType getItemType();

}
