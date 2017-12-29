package base.app.data.purchases;

import java.util.Date;

/**
 * Created by Filip on 4/20/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

/*
    This is a temporary class to track transactions in the app
 */

public class Purchase {

    private String itemName;
    private int value;
    private Date transactionDate;

    public Purchase(String itemName, int value, Date transactionDate) {
        this.itemName = itemName;
        this.value = value;
        this.transactionDate = transactionDate;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
