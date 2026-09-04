package com.space.engine.core.ecs.components;

import java.util.Objects;

public record CharComponent(char symbol) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CharComponent that = (CharComponent) o;
        return symbol == that.symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(symbol);
    }

    @Override
    public String toString() {
        return "CharComponent{" +
                symbol +
                '}';
    }
}
