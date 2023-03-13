package at.aau.itec.emmt.jpeg.stud;

import at.aau.itec.emmt.jpeg.impl.AbstractHuffmanCoder;
import at.aau.itec.emmt.jpeg.impl.RunLevel;
import at.aau.itec.emmt.jpeg.spec.BlockI;
import at.aau.itec.emmt.jpeg.spec.RunLevelI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HuffmanCoder extends AbstractHuffmanCoder {

    @Override
    public RunLevelI[] runLengthEncode(BlockI quantBlock) {

        int[] zigzagArray = zigzagScan(quantBlock.getData());
        List<RunLevel> runLevels = new ArrayList<>();


        int runLength = 0;
        for (int i = 1; i < zigzagArray.length; i++) {
            if (zigzagArray[i] == 0) {
                runLength++;
            } else {
                runLevels.add(new RunLevel(runLength, zigzagArray[i]));
                runLength = 0;
            }
        }

        // Add end-of-block marker
        runLevels.add(new RunLevel(0, 0));
        

        RunLevel[] runLevel = runLevels.toArray(new RunLevel[runLevels.size()]);

        return runLevel;
    }

    private int[] zigzagScan(int[][] array) {
        int[] result = new int[array.length * array[0].length];
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                result[index++] = array[ZIGZAG_ORDER[i * 8 + j] / 8][ZIGZAG_ORDER[i * 8 + j] % 8];
            }
        }
        return result;
    }
}
