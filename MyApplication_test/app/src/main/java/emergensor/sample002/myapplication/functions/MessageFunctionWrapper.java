package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.lib.Function;

/**普通のfunctionをタイムスタンプ付きfunctionに変える*/
public class MessageFunctionWrapper<I, O> extends AbstractFunctionWrapper<Message<I>, Message<O>, I, O> {

    public MessageFunctionWrapper(Function<I, O> function) {
        super(function);
    }

    @Override
    public Message<O> apply(Message<I> m) {
        return new Message<>(m.timestamp, function.apply(m.value)); //messageの内、valueのみにfunctionを適用
    }

}
