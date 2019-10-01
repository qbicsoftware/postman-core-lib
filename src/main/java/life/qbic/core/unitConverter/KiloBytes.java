package life.qbic.core.unitConverter;


class KiloBytes implements UnitDisplay{

    private String unit = "Kb";

    private double divisor = Math.pow(1024, 1);


    @Override
    public double convertBytesToUnit(long bytes) {
        return (double) bytes/divisor;
    }

    @Override
    public String getUnitType() {
        return this.unit;
    }
}
