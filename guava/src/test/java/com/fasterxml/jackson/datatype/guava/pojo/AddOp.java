package com.fasterxml.jackson.datatype.guava.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Marcin Kamionowski
 */
public class AddOp implements MathOp {

    private final int left;
    private final int right;

    @JsonCreator
    public AddOp(
            @JsonProperty("left") int left,
            @JsonProperty("right") int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int value() {
        return left + right;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.left;
        hash = 47 * hash + this.right;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AddOp other = (AddOp) obj;
        if (this.left != other.left) {
            return false;
        }
        return this.right == other.right;
    }

}
