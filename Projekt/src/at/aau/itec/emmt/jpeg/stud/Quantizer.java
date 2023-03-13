package at.aau.itec.emmt.jpeg.stud;

import at.aau.itec.emmt.jpeg.impl.Block;
import at.aau.itec.emmt.jpeg.spec.BlockI;
import at.aau.itec.emmt.jpeg.spec.DCTBlockI;
import at.aau.itec.emmt.jpeg.spec.QuantizationI;

public class Quantizer implements QuantizationI {

    protected int qualityFactor;

    public Quantizer() {
        this(DEFAULT_QUALITY_FACTOR);
    }

    public Quantizer(int qualityFactor) {
        this.qualityFactor = qualityFactor;
    }

    @Override
    public int[] getQuantumLuminance() {
        return QUANTUM_LUMINANCE;
    }

    @Override
    public int[] getQuantumChrominance() {
        return QUANTUM_CHROMINANCE;
    }

    @Override
    public BlockI quantizeBlock(DCTBlockI dctBlock, int compType) {
        int[][] quantizedBlock = new int[dctBlock.getData().length][dctBlock.getData()[0].length];

        int qualityScaleFactor;

        if(qualityFactor < 50){
            qualityScaleFactor = 5000 / qualityFactor;
        }else{
            qualityScaleFactor = 200 - 2 * qualityFactor;
        }

        for(int i = 0; i < quantizedBlock.length; i++){
            for(int j = 0; j < quantizedBlock[0].length; j++){
                if(compType == 0){
                    quantizedBlock[i][j] = (int) Math.round(dctBlock.getData()[i][j] / Math.min(255, Math.max(1, (getQuantumLuminance()[(i * 8) + j]*qualityScaleFactor+50)/100)));
                }else{
                    quantizedBlock[i][j] = (int) Math.round(dctBlock.getData()[i][j] / Math.min(255, Math.max(1, (getQuantumChrominance()[(i * 8) + j]*qualityScaleFactor+50)/100)));
                }
            }
        }

        return new Block(quantizedBlock);
    }

    @Override
    public void setQualityFactor(int qualityFactor) {
        this.qualityFactor = qualityFactor;
    }

}
