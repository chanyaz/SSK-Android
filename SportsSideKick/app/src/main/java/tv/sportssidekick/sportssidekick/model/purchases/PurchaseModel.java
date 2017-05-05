package tv.sportssidekick.sportssidekick.model.purchases;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.gamesparks.sdk.api.autogen.GSTypes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.solovyev.android.checkout.Billing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.GSAndroidPlatform;

/**
 * Created by Filip on 4/20/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PurchaseModel {

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnTbfN8A" +
            "arysgU8/LFki9OnZpfkULpW87PQfrIb94QSl4zkSK02BXiCFjBTQbKoTzqPQH68VgsdMJZRAdqzq4oGt5BLd" +
            "pr/BqmgMAOavxKI6G7rKLTour2gq1+7RUxfZjT94K6+luWKMD/fiODv3eZkWIJkbeetc658phTIejzrulljj" +
            "KQijfmrsut+vlhQzq84HrGbt9fO9N7iNkxGl0mcR9GZtCi55ay5hnbBC914BXIdOfAdJCoIgX0IAHbVckig0" +
            "QpclNvB/JP/PmrW0Taz4r8FpSrJbj1GMVg6TeDQhJBCsOxSSH7GPvmN7T4+Gyg5/o4fq4DwZJY9CUrr9+ZQI" +
            "DAQAB";

    private Billing billing;

    private static final String TAG = "PURCHASES";

    public Billing getBilling() {
        return billing;
    }

    private enum ProductShortCode{ // todo - change to GP ids?
        CurrencyBundle10,
        CurrencyBundle100,
        CurrencyBundle200,
        CurrencyBundle500,
        CurrencyBundle1000,
        CurrencyBundle1500,
        Bag_OfTheMonth,
        Subscription_Monthly
    }

    private class ProductPrice {
        String currencySymbol = "";
        int price;
    }

    private static PurchaseModel instance;

    private PurchaseModel(){ }

    public static PurchaseModel getInstance(){
        if(instance == null){
            instance = new PurchaseModel();
        }
        return instance;
    }

    public void initialize(Context context){
        billing = new Billing(context, new Billing.DefaultConfiguration() {
            @NonNull
            @Override
            public String getPublicKey() {
                return PUBLIC_KEY;
            }
        });
    }

    private boolean canPurchase = false;
    private static final int currency = 1; // Get from remote config - GSActiveCurrency ?
    private List<GSTypes.VirtualGood> products = new ArrayList<>();


    public void purchase(ProductShortCode shortCode, int quantity){
        if(!canPurchase){
            Log.d(TAG,"Purchase is not available at the moment!");
            return;
        }

        GSTypes.VirtualGood product = getProductByShortCode(shortCode.name());
        if(product==null){
            Log.d(TAG,"Product does not exist!");
            return;
        }

        String androidId = product.getGooglePlayProductId();
        if(androidId==null){
            Log.d(TAG," Diverting to IAPManager to pay on Google Play first!");
//          iapManager.purchase(productId: iosId) TODO Start purchase!
            return;
        }
        GSAndroidPlatform.gs().getRequestBuilder()
                .createBuyVirtualGoodsRequest()
                .setCurrencyType(currency)
                .setQuantity(quantity)
                .setShortCode(shortCode.name())
                .send(onVirtualGoodsPurchased);
    }

    private GSEventConsumer<GSResponseBuilder.BuyVirtualGoodResponse> onVirtualGoodsPurchased
            = new GSEventConsumer<GSResponseBuilder.BuyVirtualGoodResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.BuyVirtualGoodResponse response) {
            boolean result = true;
            String error = null;

            if(response == null){
                result = false;
                error = "Invalid response";
            } else {
                if(response.hasErrors()){
                    result = false;
                    error = getFirstError(response.getBaseData());
                }
            }

            if(Model.getInstance().getUserInfo() == null) {
                return;
            }

            final boolean finalResult = result;
            final String finalError = error;
            Model.getInstance().refreshUserInfo(Model.getInstance().getUserInfo().getUserId()).addOnCompleteListener(
                    new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if(task.isSuccessful()){
                                EventBus.getDefault().post(new PurchaseCompletedEvent(finalResult, finalError));
                                canPurchase = true;
                            }
                        }
                    }
            );
        }
    };

    public void consume(ProductShortCode shortCode, int quantity){
        GSTypes.VirtualGood product = getProductByShortCode(shortCode.name());
        if(product==null){
            Log.d(TAG,"Product does not exist!");
            return;
        }


        GSAndroidPlatform.gs().getRequestBuilder()
                .createConsumeVirtualGoodRequest()
                .setQuantity(quantity)
                .setShortCode(shortCode.name())
                .send(onVirtualGoodsConsumed);
    }

    private GSEventConsumer<GSResponseBuilder.ConsumeVirtualGoodResponse> onVirtualGoodsConsumed = new GSEventConsumer<GSResponseBuilder.ConsumeVirtualGoodResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.ConsumeVirtualGoodResponse response) {
            boolean result = true;
            String error = null;

            if(response == null){
                result = false;
                error = "Invalid response";
            } else {
                if(response.hasErrors()){
                    result = false;
                    error = getFirstError(response.getBaseData());
                }
            }

            if(Model.getInstance().getUserInfo() == null) {
                return;
            }

            final boolean finalResult = result;
            final String finalError = error;
            Model.getInstance().refreshUserInfo(Model.getInstance().getUserInfo().getUserId()).addOnCompleteListener(
                    new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if(task.isSuccessful()){
                                EventBus.getDefault().post(new ProductConsumedEvent(finalResult, finalError));

                            }
                        }
                    }
            );
        }
    };

    private GSTypes.VirtualGood getProductByShortCode(String shortCode){
        for(GSTypes.VirtualGood product : products){
            if(product.getShortCode().equals(shortCode)){
                return product;
            }
        }
        return null;
    }

    private String getFirstError(Map<String, Object> errors){
        String error = "";
        for(Map.Entry<String,Object> entry : errors.entrySet()){
            if(entry.getKey().contains("error")){
                error = entry.getKey() + " : " + entry.getValue();
                break;
            }
        }
        return error;
    }

    public ProductPrice getGooglePlayProductPriceByShortCode(String shortCode){
        GSTypes.VirtualGood product = getProductByShortCode(shortCode);

        if(product==null){
            Log.d(TAG,"Product does not exist!");
            return null;
        }

        String androidId = product.getGooglePlayProductId();
        if(androidId==null){
            Log.d(TAG," Diverting to IAPManager to pay on Google Play first!");
            return null;
        }
//        guard let iTunesProduct = self.iapManager.products[iosId] else {
//            return nil
//        }
//
//        return ProductPrice(
//                currencySymbol: iTunesProduct.priceLocale.currencySymbol ?? "",
//                price: iTunesProduct.price
//        )
        // TODO Extract product price info from Google Play!
        ProductPrice productPrice = new ProductPrice();
        productPrice.price = 0;
        productPrice.currencySymbol="$";
        return new ProductPrice();
    }

    public void updateProductList(){
        canPurchase = false;
        GSAndroidPlatform.gs().getRequestBuilder()
                .createListVirtualGoodsRequest()
                .send(new GSEventConsumer<GSResponseBuilder.ListVirtualGoodsResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.ListVirtualGoodsResponse response) {
                        if(!response.hasErrors()){
                            List<GSTypes.VirtualGood> goods = response.getVirtualGoods();
                            if(goods==null){
                                Log.d(TAG,"No virtual goods configured on GS!");
                            } else {
                                products = goods;
                                extractProductIds();
                            }
                        }
                    }
                });
    }

    /**
     * Extract the Google Play product id's here and pass them to the IAPManager to retreive
     * the list from Google Play
     */
    private void extractProductIds() {
        List<String> productIds = new ArrayList<>();
        for(GSTypes.VirtualGood product : products){
            String productId = product.getGooglePlayProductId();
            if(productId!=null){
                productIds.add(productId);
            }
//            self.iapManager.getProductsBy(productIds: productIds) TODO Get products info from Google Play!
        }
    }

    public void onProductListUpdated(){
        canPurchase = true;
    }

    // TODO Check this?
    public void onReceiptRefreshed(String receipt){
        GSAndroidPlatform.gs().getRequestBuilder()
                .createGooglePlayBuyGoodsRequest()
                .setRequestId("") // TODO Check this?
                .setSignature("") // TODO Check this?
                .setSignedData("") // TODO Check this?
                .setUniqueTransactionByPlayer(false)
                .send(onVirtualGoodsPurchased);
    }
}
