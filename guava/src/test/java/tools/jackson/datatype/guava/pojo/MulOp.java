package tools.jackson.datatype.guava.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Marcin Kamionowski
 */
public class MulOp implements MathOp {

    private final int left;
    private final int right;

    @JsonCreator
    public MulOp(
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
        return left * right;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.left;
        hash = 67 * hash + this.right;
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
        final MulOp other = (MulOp) obj;
        if (this.left != other.left) {
            return false;
        }
        return this.right == other.right;
    }

}
