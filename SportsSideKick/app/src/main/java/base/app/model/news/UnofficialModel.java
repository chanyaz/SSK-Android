package base.app.model.news;

/**
 * Created by Djordje Krutil on 27.12.2016..
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
