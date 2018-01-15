package emergensor.sample002.myapplication.block;

import emergensor.sample002.myapplication.lib.Consumer;

import java.util.ArrayList;

/**
 * output(～)が呼び出されたときに、指定したlistenerに～を通知するクラス
 */
public class Block<M> {

    protected final ArrayList<Consumer<M>> listeners = new ArrayList<>();


    public void addListener(Consumer<M> listener) {
        listeners.add(listener);
    }

    public void output(M m) {
        for (Consumer<M> listener : listeners) {
            listener.accept(m);
        }
    }

}
