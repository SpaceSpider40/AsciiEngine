package com.engine.graphics;

import com.engine.math.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorTest {

    @Test
    void defaultConstructorCreatesBlack() {
        Color color = new Color();

        assertEquals(0, color.getR());
        assertEquals(0, color.getG());
        assertEquals(0, color.getB());
    }

    @Test
    void rgbConstructorStoresValues() {
        Color color = new Color(12, 34, 56);

        assertEquals(12, color.getR());
        assertEquals(34, color.getG());
        assertEquals(56, color.getB());
    }

    @Test
    void packCombinesRgbChannels() {
        Color color = new Color(255, 128, 64);

        int packed = color.pack();

        assertEquals(255, Color.unpackR(packed));
        assertEquals(128, Color.unpackG(packed));
        assertEquals(64, Color.unpackB(packed));
    }

    @Test
    void unpackCreatesColorFromPackedInt() {
        int packed = new Color(10, 20, 30).pack();

        Color color = Color.unpack(packed);

        assertEquals(10, color.getR());
        assertEquals(20, color.getG());
        assertEquals(30, color.getB());
    }

    @Test
    void packedConstructorUnpacksChannels() {
        int packed = new Color(1, 2, 3).pack();

        Color color = new Color(packed);

        assertEquals(1, color.getR());
        assertEquals(2, color.getG());
        assertEquals(3, color.getB());
    }

    @Test
    void settersClampValuesToByteRange() {
        Color color = new Color();

        color.setR((short) -10);
        color.setG((short) 300);
        color.setB((short) 128);

        assertEquals(0, color.getR());
        assertEquals(255, color.getG());
        assertEquals(128, color.getB());
    }

    @Test
    void asVectorReturnsRgbAsVector3() {
        Color color = new Color(5, 10, 15);

        Vector3 vector = color.asVector();

        assertEquals(5, vector.x);
        assertEquals(10, vector.y);
        assertEquals(15, vector.z);
    }

    @Test
    void predefinedColorsHaveExpectedValues() {
        assertEquals(255, Color.RED.getR());
        assertEquals(0, Color.RED.getG());
        assertEquals(0, Color.RED.getB());

        assertEquals(255, Color.WHITE.getR());
        assertEquals(255, Color.WHITE.getG());
        assertEquals(255, Color.WHITE.getB());

        assertEquals(0, Color.BLACK.getR());
        assertEquals(0, Color.BLACK.getG());
        assertEquals(0, Color.BLACK.getB());
    }
}