package net.sourceforge.vrapper.keymap;

import net.sourceforge.vrapper.log.VrapperLog;
import net.sourceforge.vrapper.utils.Function;

public class ConvertingState<T1, T2> implements State<T1> {

    private final Function<T1, T2> converter;
    private final State<T2> wrapped;

    public ConvertingState(Function<T1, T2> converter, State<T2> wrapped) {
        this.converter = converter;
        this.wrapped = wrapped;
    }

    public Transition<T1> press(KeyStroke key) {
        Transition<T2> transition = wrapped.press(key);
        if (transition != null)
            return new ConvertingTransition<T1, T2>(converter, transition.getValue(), transition.getNextState());
        return null;
    }

    public State<T1> union(State<T1> other) {
        if (other instanceof ConvertingState<?, ?>)
        	VrapperLog.info("TODO: implement ConvertingState's union efficently");
        return new UnionState<T1>(this, other);
    }
}
