package life.qbic.core.unitConverter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnitConverterFactoryTest {

    @Test
    public void determineBestUnitType() {
        assertEquals("Tb", UnitConverterFactory.determineBestUnitType(5697856536651L).getUnitType());
        assertEquals("Gb", UnitConverterFactory.determineBestUnitType(5697856651L).getUnitType());
        assertEquals("Mb", UnitConverterFactory.determineBestUnitType(785656651L).getUnitType());
        assertEquals("Kb", UnitConverterFactory.determineBestUnitType(569761L).getUnitType());
        assertEquals("bytes", UnitConverterFactory.determineBestUnitType(61L).getUnitType());
    }

}
