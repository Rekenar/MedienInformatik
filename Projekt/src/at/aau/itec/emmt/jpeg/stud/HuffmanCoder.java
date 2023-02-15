package at.aau.itec.emmt.jpeg.stud;

import at.aau.itec.emmt.jpeg.impl.AbstractHuffmanCoder;
import at.aau.itec.emmt.jpeg.impl.RunLevel;
import at.aau.itec.emmt.jpeg.spec.BlockI;
import at.aau.itec.emmt.jpeg.spec.RunLevelI;

public class HuffmanCoder extends AbstractHuffmanCoder {

    @Override
    public RunLevelI[] runLengthEncode(BlockI quantBlock) {

        int[] temp = new int[quantBlock.getData().length*quantBlock.getData()[0].length];

        int index = 0;

        for(int i = 0; i < quantBlock.getData().length; i++){
            for(int j = 0; j < quantBlock.getData()[0].length; j++){
                temp[index++] = quantBlock.getData()[i][j];
            }
        }
        int[] array = new int[temp.length];

        for(int i = 0; i < temp.length; i++){
            array[i] = temp[ZIGZAG_ORDER[i]];

        }

        int counter = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] != 0){
                counter++;
            }
        }
        RunLevel[] runLevels = new RunLevel[counter];

        int run = 0;
        counter = 0;
        for(int i = 0; i < array.length+1; i++){
            if(i == 0 && array[i] != 0){
                continue;
            }

            if(counter == runLevels.length-1){
                runLevels[counter] = new RunLevel(0,0);
                break;
            }

            if(array[i] == 0){
                run++;
            }
            else if(array[i] != 0){
                runLevels[counter]= new RunLevel(run, array[i]);
                counter++;
                run = 0;
            }

        }

        return runLevels;
    }
}
