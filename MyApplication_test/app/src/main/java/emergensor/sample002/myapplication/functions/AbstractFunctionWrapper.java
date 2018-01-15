package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Function;

/**FIを受け取ってFOを返す関数で構成された、Iを受け取ってOを返す関数*/
public abstract class AbstractFunctionWrapper<I, O, FI, FO> extends AbstractFunction<I, O> {

    protected final Function<FI, FO> function;

    public AbstractFunctionWrapper(Function<FI, FO> function) {
        this.function = function;
    }

}
