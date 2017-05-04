package tv.sportssidekick.sportssidekick.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import tv.sportssidekick.sportssidekick.model.purchases.PurchaseModel;

/**
 * Created by Filip on 4/20/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class BillingActivity extends AppCompatActivity {

    private static final String TAG = "Billing Activity";

    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        // your code here
    }

    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(Inventory.Products products) {
            // your code here
            Log.d(TAG, "Loaded products: " + products.size());
        }
    }

    private ActivityCheckout mCheckout;
    private Inventory mInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckout = Checkout.forActivity(this, PurchaseModel.getInstance().getBilling());
        mCheckout.start();

        mCheckout.createPurchaseFlow(new PurchaseListener());

        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.IN_APP, "pack1"), new InventoryCallback()); // Link with Purchase model
    }

    @Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCheckout.onActivityResult(requestCode, resultCode, data);
    }


}
