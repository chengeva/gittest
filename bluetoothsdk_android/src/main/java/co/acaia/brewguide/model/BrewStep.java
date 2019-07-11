package co.acaia.brewguide.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BrewStep {

    public enum BrewStepType {
        BREW_STEP_TYPE_AMOUNT,
        BREW_STEP_ADD_DURATION,
        BREW_STEP_ADD_TEXT,
        BREW_STEP_ADD_WEIGHT_TIME,
        BREW_STEP_FLOW_INDICATOR,
    }

    public enum BrewStepSubType {
        BREW_STEP_SUB_WATER,
        BREW_STEP_SUB_COFFEE,
        BREW_STEP_SUB_STIR,
        BREW_STEP_SUB_PRESS,
        BREW_STEP_SUB_WAIT,
        BREW_STEP_SUB_POUR,
    }

    // Brewstep Fields
    public int index;
    public String message;
    public int weight;
    public int time;
    public int brewStepType;
    public int brewStepSubType;

    //Pearl S actions
    public int autoPause0;
    public int autoPause1;
    public int alertSound0;
    public int alertSound1;

    // Constructor, use only index and step type for now. Future to do make abstract class for BrewStep.
    // Most important is index and determine the main type of Brewstep.

    public BrewStep(int brewstepIndex, int brewStepType){
        this.index=brewstepIndex;
        this.brewStepType=brewStepType;
    }

    /**
     *
     * @param brewStepJsonStr
     * @return
     */
    // Construct a list of brewstep from database json string.
    public static ArrayList<BrewStep> brewStepsFromJsonString(String brewStepJsonStr){
        ArrayList<BrewStep> brewSteps =new ArrayList<>();

        /** Sample Brewstep JSON
         *     "weight" : 180,
         *     "autoPause1" : 0,
         *     "autoPause0" : 0,
         *     "time" : 0,
         *     "alertSound0" : 0,
         *     "feedback" : "",
         *     "message" : "Add coffee",
         *     "alertSound1" : 0,
         *     "brewStepSubType" : 1,
         *     "brewStepType" : 0,
         *     "index" : 1
         */
        try{
            JSONArray itemArray=new JSONArray(brewStepJsonStr);
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject currObject = itemArray.getJSONObject(i);
                int stepWeight=((int)(currObject.getDouble("weight")*10));
                int autoPause0=currObject.getInt("autoPause0");
                int autoPause1=currObject.getInt("autoPause1");
                int alertSound0=currObject.getInt("alertSound0");
                int alertSound1=currObject.getInt("alertSound1");
                int brewStepSubType=currObject.getInt("brewStepSubType");
                int brewStepType=currObject.getInt("brewStepType");
                int index=currObject.getInt("index");
                int brewtime=currObject.getInt("time");
                String stepMessage=currObject.getString("message");
                Log.v("Brewstep",stepMessage);

                BrewStep brewStep=new BrewStep(index,brewStepType);
                brewStep.time=brewtime;
                brewStep.weight=stepWeight;
                brewStep.message=stepMessage;
                brewStep.autoPause0=autoPause0;
                brewStep.autoPause1=autoPause1;
                brewStep.alertSound0=alertSound0;
                brewStep.alertSound1=alertSound1;
                brewStep.brewStepSubType=brewStepSubType;

                brewSteps.add(brewStep);

            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return brewSteps;
    }

}
