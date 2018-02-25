package base.app.data.wall.sharing;

public interface Shareable {

    void incrementShareCount(SharingManager.ShareTarget shareTarget);

    SharingManager.ItemType getItemType();

}
