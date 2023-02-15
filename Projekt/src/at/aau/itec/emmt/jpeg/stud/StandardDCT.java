package at.aau.itec.emmt.jpeg.stud;

import at.aau.itec.emmt.jpeg.impl.DCTBlock;
import at.aau.itec.emmt.jpeg.spec.BlockI;
import at.aau.itec.emmt.jpeg.spec.DCTBlockI;
import at.aau.itec.emmt.jpeg.spec.DCTI;

public class StandardDCT implements DCTI {

    @Override
    public DCTBlockI forward(BlockI b) {
        double[][] dctCoeffs = new double[b.getData().length][b.getData()[0].length];
        double sum = 0;
        double c1;
        double c2;


        for(int u = 0; u < dctCoeffs.length; u++){
            c1 = 1;
            if(u == 0){
                c1 = 1/Math.sqrt(2);
            }
            for(int v = 0; v < dctCoeffs.length; v++){
                c2 = 1;
                if(v == 0){
                    c2 = 1/Math.sqrt(2);
                }
                for(int i = 0; i < b.getData().length; i++){
                    for(int j = 0; j < b.getData().length; j++){
                        sum += ((b.getData()[j][i]-Math.pow(2, 8-1)) * Math.cos((((2 * i + 1) * u * Math.PI) / 16)) * Math.cos(((2 * j + 1) * v * Math.PI / (16))));
                    }
                }
                dctCoeffs[u][v] = ((c1*c2) / 4) * sum;
                sum = 0;
            }
        }

        return new DCTBlock(dctCoeffs);
    }

}
