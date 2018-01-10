package base.app.data.news;

/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class OfficialModel {

    public static OfficialModel instance;

    public OfficialModel(){}

    public OfficialModel getInstance ()
    {
        if (instance == null)
        {
            instance = new OfficialModel();
        }
        return instance;
    }
}
