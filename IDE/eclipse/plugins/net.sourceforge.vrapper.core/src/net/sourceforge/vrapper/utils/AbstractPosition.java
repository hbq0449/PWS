package net.sourceforge.vrapper.utils;

public abstract class AbstractPosition implements Position {

    public int compareTo(Position o) {
        int diff = getModelOffset()-o.getModelOffset();
        return (int) Math.signum(diff);
    }
    
    @Override
    public int hashCode() {
        // FIXME: FindBugs
        throw new UnsupportedOperationException("method not yet implemented");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            return compareTo((Position)obj) == 0;
        }
        return false;
    }

}
