package base.app.data.news;

/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class UnofficialModel {

    private static UnofficialModel instance;

    public UnofficialModel() {}

    public UnofficialModel getInstance()
    {
        if (instance==null)
        {
            instance = new UnofficialModel();
        }
        return instance;
    }

}
