package base.app.model.purchases;

/**
 * Created by Filip on 5/4/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ProductConsumedEvent {

    boolean consumeSuccessful;
    String error;

    public ProductConsumedEvent(boolean purchaseSuccessful, String error) {
        this.consumeSuccessful = purchaseSuccessful;
        this.error = error;
    }

    public boolean isConsumeSuccessful() {
        return consumeSuccessful;
    }

    public void setConsumeSuccessful(boolean consumeSuccessful) {
        this.consumeSuccessful = consumeSuccessful;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
