package emergensor.sample002.myapplication.functions;

import emergensor.sample002.myapplication.lib.Complex;

public class ComplexAbsoluteFunction extends AbstractFunction<Complex, Double> {

    @Override
    public Double apply(Complex data) {
        return data.abs();
    }//複素数の絶対値を返す

}
