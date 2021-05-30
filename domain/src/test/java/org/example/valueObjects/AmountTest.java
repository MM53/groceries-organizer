package org.example.valueObjects;

import org.example.exceptions.UnitMismatchException;
import org.example.units.Pieces;
import org.example.units.Volume;
import org.example.units.Weight;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmountTest {

    @Test
    public void compare_sameUnit() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2 = new Amount(100, Weight.GRAM);

        assertTrue(amount1.isMoreThan(amount2));
    }

    @Test
    public void compare_sameUnitType() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2 = new Amount(0.1, Weight.KILOGRAM);

        assertTrue(amount1.isMoreThan(amount2));
    }

    @Test
    public void compare_differentUnit() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2 = new Amount(100, Volume.MILLILITER);

        assertThrows(UnitMismatchException.class, () -> amount1.isMoreThan(amount2));
    }

    @Test
    public void equals_sameUnitType() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2 = new Amount(0.5, Weight.KILOGRAM);

        assertEquals(amount1, amount2);
    }

    @Test
    public void equals_differentUnit() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2 = new Amount(0.5, Volume.LITER);

        assertThrows(UnitMismatchException.class, () -> amount1.equals(amount2));
    }

    @Test
    public void convertUnit_Weight_success() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2 = new Amount(1, Weight.KILOGRAM);


        assertEquals(amount1.getValueInUnit(Weight.KILOGRAM), 0.5);
        assertEquals(amount2.getValueInUnit(Weight.GRAM), 1000);
    }

    @Test
    public void convertUnit_Volume_success() {
        Amount amount1 = new Amount(500, Volume.MILLILITER);
        Amount amount2= new Amount(1, Volume.LITER);

        assertEquals(amount1.getValueInUnit(Volume.LITER), 0.5);
        assertEquals(amount2.getValueInUnit(Volume.MILLILITER), 1000);
    }

    @Test
    public void convertUnit_differentUnitType() {
        Amount amount1 = new Amount(500, Volume.MILLILITER);
        Amount amount2= new Amount(1, Weight.KILOGRAM);

        assertThrows(UnitMismatchException.class, () -> amount1.getValueInUnit(Weight.GRAM));
        assertThrows(UnitMismatchException.class, () -> amount2.getValueInUnit(Pieces.PIECES));
    }

    @Test
    public void addAmount_Weight_success() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2= new Amount(1, Weight.KILOGRAM);

        assertEquals(amount1.add(amount2), new Amount(1.5, Weight.KILOGRAM));
        assertEquals(amount2.add(amount1), new Amount(1.5, Weight.KILOGRAM));
    }

    @Test
    public void addAmount_Volume_success() {
        Amount amount1 = new Amount(500, Volume.MILLILITER);
        Amount amount2= new Amount(1, Volume.LITER);

        assertEquals(amount1.add(amount2), new Amount(1.5, Volume.LITER));
        assertEquals(amount2.add(amount1), new Amount(1.5, Volume.LITER));
    }

    @Test
    public void addAmount_differentUnitType() {
        Amount amount1 = new Amount(500, Volume.MILLILITER);
        Amount amount2= new Amount(1, Weight.KILOGRAM);

        assertThrows(UnitMismatchException.class, () -> amount1.add(amount2));
        assertThrows(UnitMismatchException.class, () -> amount2.add(amount1));
    }

    @Test
    public void subAmount_Weight_success() {
        Amount amount1 = new Amount(500, Weight.GRAM);
        Amount amount2= new Amount(1, Weight.KILOGRAM);

        assertEquals(amount1.sub(amount2), new Amount(-0.5, Weight.KILOGRAM));
        assertEquals(amount2.sub(amount1), new Amount(500, Weight.GRAM));
    }

    @Test
    public void subAmount_Volume_success() {
        Amount amount1 = new Amount(500, Volume.MILLILITER);
        Amount amount2= new Amount(1, Volume.LITER);

        assertEquals(amount1.sub(amount2), new Amount(-500, Volume.MILLILITER));
        assertEquals(amount2.sub(amount1), new Amount(0.5, Volume.LITER));
    }

    @Test
    public void aubAmount_differentUnitType() {
        Amount amount1 = new Amount(500, Volume.MILLILITER);
        Amount amount2= new Amount(1, Weight.KILOGRAM);

        assertThrows(UnitMismatchException.class, () -> amount1.sub(amount2));
        assertThrows(UnitMismatchException.class, () -> amount2.sub(amount1));
    }
}