package co.acaia.communications.events;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by kenkuan on 8/2/16.
 */
public class Weight implements Serializable {

    public final static String TEXT_GRAM = "g";
    public final static String TEXT_OUNCE = "oz";

    public static final int UNIT_GRAM = 0;
    public static final int UNIT_OZ = 1;
    public static Weight ZeroWeight = new Weight(0, 0);

    private final double value;
    private final int unit;

    public Weight(double value, int unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public int getUnit() {
        return unit;
    }

    public String getUnitText() {
        switch (unit) {
            case UNIT_OZ:
                return TEXT_OUNCE;
            case UNIT_GRAM:
            default:
                return TEXT_GRAM;
        }
    }

    @Override
    public String toString() {
        return getDisplayString();
    }

    public String getDisplayString() {
        return String.format("%.1f%s", getValue(), getUnitText());
    }

    public String getSimpleDisplayString() {
        return String.format("%.0f%s", getValue(), getUnitText());
    }

    public double getWeightInGram() {
        if (unit == UNIT_GRAM) {
            return value;
        }
        return value * 28.3495;
    }

    public Weight minus(@NonNull Weight rhs) {
        final double newValueInGram = getWeightInGram() - rhs.getWeightInGram();
        final double newValue = convertFromGram(newValueInGram, unit);
        return new Weight(newValue, unit);
    }

    private static double convertFromGram(double gram, int unit) {
        if (unit == UNIT_GRAM) {
            return gram;
        } else {
            return gram * 0.03527;
        }

    }

//    public static Weight fromGram(double gram, Context context) {
//        final int unit = AcaiaSettings.getInstance().getScaleUnit(context);
//        return new Weight(convertFromGram(gram, unit), unit);
//    }

    public Weight add(int value) {
        return new Weight(this.value + value, unit);
    }
}
