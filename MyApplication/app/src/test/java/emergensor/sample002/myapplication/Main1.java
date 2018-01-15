package emergensor.sample002.myapplication;

import org.junit.Test;

import java.text.NumberFormat;

import emergensor.sample002.myapplication.block.Block;
import emergensor.sample002.myapplication.block.BlockBuilder;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.block.filter.FunctionFilter;
import emergensor.sample002.myapplication.block.source.IntervalSource;
import emergensor.sample002.myapplication.functions.AbstractFunction;
import emergensor.sample002.myapplication.functions.MessageFunctionWrapper;
import emergensor.sample002.myapplication.functions.NormFunction;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Function;

import static emergensor.sample002.myapplication.block.BlockBuilder.*;

/**
 * Created by yoshi on 2017/12/09.
 */

public class Main1 {

    @Test
    public void test1() throws Exception {
        Block<String> source = build(new Block<String>())
                .add(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        System.out.println(s);
                    }
                })
                .get();

        source.output("abc");
    }

    @Test
    public void test2() throws Exception {
        Block<Double> source = build(new Block<Double>())
                .add(build(new FunctionFilter<>(new AbstractFunction<Double, Double>() {
                    @Override
                    public Double apply(Double x) {
                        return x * (x + 1) / 2;
                    }
                }))
                        .add(new Consumer<Double>() {
                            @Override
                            public void accept(Double a) {
                                System.out.println(a);
                            }
                        })
                        .get())
                .get();

        source.output(5.0);
        source.output(100.0);
        source.output(10000.0);
    }

    @Test
    public void test3() throws Exception {
        Block<String> source = build(new Block<String>())
                .add(build(new FunctionFilter<>(new AbstractFunction<String, Double>() {
                    @Override
                    public Double apply(String s) {
                        return (double) s.length();
                    }
                }))
                        .add(build(new FunctionFilter<>(new AbstractFunction<Double, Double>() {
                            @Override
                            public Double apply(Double x) {
                                return x * (x + 1) / 2;
                            }
                        }))
                                .add(new Consumer<Double>() {
                                    @Override
                                    public void accept(Double a) {
                                        System.out.println(a);
                                    }
                                })
                                .get())
                        .get())
                .get();

        source.output("a");
        source.output("ab");
        source.output("abc");
    }

    @Test
    public void test4() throws Exception {
        Block<String> source = build(new Block<String>())
                .add(build(new FunctionFilter<>(new AbstractFunction<String, String>() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                }))
                        .add(new Consumer<String>() {
                            @Override
                            public void accept(String a) {
                                System.out.println(a);
                            }
                        })
                        .get())

                .add(build(new FunctionFilter<>(new AbstractFunction<String, String>() {
                    @Override
                    public String apply(String s) {
                        return s + s;
                    }
                }))
                        .add(new Consumer<String>() {
                            @Override
                            public void accept(String a) {
                                System.out.println(a);
                            }
                        })
                        .get())

                .add(build(new FunctionFilter<>(new AbstractFunction<String, String>() {
                    @Override
                    public String apply(String s) {
                        return s + s + s;
                    }
                }))
                        .add(new Consumer<String>() {
                            @Override
                            public void accept(String a) {
                                System.out.println(a);
                            }
                        })
                        .get())
                .get();

        source.output("a");
        source.output("ab");
        source.output("abc");
    }

    @Test
    public void test5() throws Exception {
        IntervalSource source = build(new IntervalSource(1000))
                .add(build(new FunctionFilter<>(new AbstractFunction<Message<Long>, Long>() {
                    @Override
                    public Long apply(Message<Long> m) {
                        return m.timestamp;
                    }
                }))
                        .add(new Consumer<Long>() {
                            @Override
                            public void accept(Long l) {
                                System.out.println(l);
                            }
                        })
                        .get())

                .add(build(new FunctionFilter<>(new AbstractFunction<Message<Long>, Integer>() {
                    @Override
                    public Integer apply(Message<Long> m) {
                        return (int) (long) m.value;
                    }
                }))
                        .add(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer a) {
                                System.out.println(a);
                            }
                        })
                        .get())
                .get();

        source.init();
        source.start();
        Thread.sleep(10000);
        source.stop();
    }

    @Test
    public void test6() throws Exception {
        System.out.println(Thread.currentThread().getName());
    }

    @Test
    public void test7() throws Exception {
        IntervalSource source = build(new IntervalSource(1000))
                .add(bfm(new AbstractFunction<Long, Long>() {
                    public Long apply(Long l) {
                        return l * 10;
                    }
                })
                        .add(build(new FunctionFilter<>(new AbstractFunction<Message<Long>, String>() {
                            @Override
                            public String apply(Message<Long> m) {
                                return m.timestamp + "," + m.value;
                            }
                        }))
                                .add(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) {
                                        System.out.println(s);
                                    }
                                })
                                .get())
                        .get())
                .get();
        source.init();
        source.start();
        Thread.sleep(20000);
        source.stop();
    }

    @Test
    public void test8() throws Exception {
        FunctionFilter<Message<String>, String> a = build(new FunctionFilter<>(new AbstractFunction<Message<String>, String>() {
            @Override
            public String apply(Message<String> m) {
                return m.timestamp + "," + m.value;
            }
        }))
                .add(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        System.out.println(s);
                    }
                })
                .get();

        FunctionFilter<Message<Long>, Message<String>> b = bfm(new AbstractFunction<Long, String>() {
            public String apply(Long l) {
                return NumberFormat.getNumberInstance().format(l);
            }
        })
                .add(a)
                .get();

        FunctionFilter<Message<Long>, Message<Long>> c = bfm(new AbstractFunction<Long, Long>() {
            @Override
            public Long apply(Long l) {
                return l * (l + 1) / 2;
            }
        })
                .add(b)
                .get();

        IntervalSource source = build(new IntervalSource(1000))
                .add(bfm(new AbstractFunction<Long, Long>() {
                    public Long apply(Long l) {
                        return l * 10;
                    }
                })
                        .add(c)
                        .get())
                .get();
        source.init();
        source.start();
        Thread.sleep(20000);
        source.stop();
    }

    /*
                .add(build(new FunctionFilter<>(new AbstractFunction<Message<Long>, Long>() {
                    @Override
                    public Long apply(Message<Long> m) {
                        return m.timestamp;
                    }
                }))
                        .add(new Consumer<Long>() {
                            @Override
                            public void accept(Long l) {
                                System.out.println(l);
                            }
                        })
                        .get())

                .add(build(new FunctionFilter<>(new AbstractFunction<Message<Long>, Integer>() {
                    @Override
                    public Integer apply(Message<Long> m) {
                        return (int) (long) m.value;
                    }
                }))
                        .add(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer a) {
                                System.out.println(a);
                            }
                        })
                        .get())
                .get();
*/

    private <I, O> BlockBuilder<FunctionFilter<Message<I>, Message<O>>, Message<O>> bfm
            (Function<I, O> function) {
        return build(new FunctionFilter<>(new MessageFunctionWrapper<>(function)));
    }

}
