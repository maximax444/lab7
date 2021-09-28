package common.Task;

import java.io.Serializable;

public enum UnitOfMeasure implements Serializable {
    KILOGRAMS,
    PCS,
    MILLILITERS;

    private String text;

    UnitOfMeasure(String text) {
        this.text = text;
    }


    UnitOfMeasure() {

    }
    public UnitOfMeasure searchVariants() {
        switch(text) {
            case "KILOGRAMS":
                return KILOGRAMS;
            case "PCS":
                return PCS;
            case "MILLILITERS":
                return MILLILITERS;
        }
        return null;
    }
}
