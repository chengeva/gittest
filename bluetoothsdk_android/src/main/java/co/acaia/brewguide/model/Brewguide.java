package co.acaia.brewguide.model;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Brewguide Object to handle Brewguide data from Parse.
 *
 */
public class Brewguide {

    private String beanName;
    private ArrayList<BrewStep> brewSteps;

    /**
     * Init a Brewguide object from Parse Object
     * @param brewguideParseObject
     */
    public Brewguide(ParseObject brewguideParseObject){
        brewSteps=BrewStep.brewStepsFromJsonString(brewguideParseObject.getString("brewStepsJson"));
    }

    public ArrayList<BrewStep> getBrewSteps()
    {
        return brewSteps;
    }

    public short numOfBrewguidePages()
    {
        return (short)(brewSteps.size()+2);
    }
}
