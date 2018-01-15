package emergensor.sample002.myapplication.block;

import emergensor.sample002.myapplication.lib.Consumer;

/**
 * 「Oを出力するブロックB」の設定をするための物体
 */
public class BlockBuilder<B extends Block<O>, O> {

    private B block;

    public BlockBuilder(B block) {
        this.block = block;
    }

    public static <B extends Block<M>, M> BlockBuilder<B, M> build(B block) {
        return new BlockBuilder<>(block); //buildはnew BlockBuilder<>と同一
    }

    public BlockBuilder<B, O> add(Consumer<O> consumer) { //consumerが、このブロック.output()の引数を受け取る
        block.addListener(consumer);
        return this; //戻り値が宣言されたクラスと同じであるため、add().add().add()のように繋げられる
    }

    public <B2 extends Block<O2> & Consumer<O>, O2> BlockBuilder<B, O> add(BlockBuilder<B2, O2> blockBuilder) { //「Consumerを実装しているBlockBuilder」が、このブロック.output()の引数を受け取る
        block.addListener(blockBuilder.block);
        return this;
    }

    public B get() {
        return block;
    } //保持しているブロックを返す

}
