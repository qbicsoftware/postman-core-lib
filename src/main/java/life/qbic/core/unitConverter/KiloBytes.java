package life.qbic.core.unitConverter;


class KiloBytes implements UnitDisplay{

    private String unit = "Kb";

    private double divisor = Math.pow(10, 3);


    @Override
    public double convertBytesToUnit(long bytes) {
        return (double) bytes/divisor;
    }

    @Override
    public String getUnitType() {
        return this.unit;
    }
}
