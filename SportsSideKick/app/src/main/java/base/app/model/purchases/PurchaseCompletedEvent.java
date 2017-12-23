package base.app.model.purchases;

/**
 * Created by Filip on 5/4/2017.
 */

public class PurchaseCompletedEvent {

    boolean purchaseSuccessful;
    String error;

    public PurchaseCompletedEvent(boolean purchaseSuccessful, String error) {
        this.purchaseSuccessful = purchaseSuccessful;
        this.error = error;
    }

    public boolean isPurchaseSuccessful() {
        return purchaseSuccessful;
    }

    public void setPurchaseSuccessful(boolean purchaseSuccessful) {
        this.purchaseSuccessful = purchaseSuccessful;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
