package base.app.data.sharing;

public interface Shareable {

    void incrementShareCount(ShareHelper.ShareTarget shareTarget);

    ShareHelper.ItemType getItemType();

}
