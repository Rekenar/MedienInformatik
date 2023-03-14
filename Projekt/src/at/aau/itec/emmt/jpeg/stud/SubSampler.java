package at.aau.itec.emmt.jpeg.stud;

import at.aau.itec.emmt.jpeg.impl.Component;
import at.aau.itec.emmt.jpeg.impl.YUVImage;
import at.aau.itec.emmt.jpeg.spec.SubSamplerI;
import at.aau.itec.emmt.jpeg.spec.YUVImageI;

public class SubSampler implements SubSamplerI {

    @Override
    public YUVImageI downSample(YUVImageI yuvImg, int samplingRatio) {
        if(samplingRatio > 3 || samplingRatio < 0){
            throw new IllegalArgumentException("SamplingRatio cannot be more than 3 or lower than 0");
        }

        int height = yuvImg.getComponent(0).getSize().height;
        int width = yuvImg.getComponent(0).getSize().width;
        int[][] y = yuvImg.getComponent(0).getData();
        int[][] u = yuvImg.getComponent(1).getData();
        int[][] v = yuvImg.getComponent(2).getData();

        int[][] u1 = new int[height][width/2];
        int[][] v1 = new int[height][width/2];

        int[][] u2 = new int[height/2][width/2];
        int[][] v2 = new int[height/2][width/2];


        if (samplingRatio == 1) {
            for (int i = 0; i < u1.length; i++) {
                for (int j = 0; j < u1[i].length; j++) {
                    u1[i][j] = u[i][2*j];
                    v1[i][j] = v[i][2*j];
                }
            }
            return new YUVImage(new Component(y, YUVImageI.Y_COMP), new Component(u1, YUVImageI.CB_COMP), new Component(v1, YUVImageI.CR_COMP), samplingRatio);
        }

        else if (samplingRatio == 2){
            for (int i = 0; i < u2.length; i++) {
                for (int j = 0; j < u2[i].length; j++) {
                    u2[i][j] = u[2*i][2*j];
                    v2[i][j] = v[2*i][2*j];
                }
            }
            return new YUVImage(new Component(y, YUVImageI.Y_COMP), new Component(u2, YUVImageI.CB_COMP), new Component(v2, YUVImageI.CR_COMP), samplingRatio);
        }
        return new YUVImage(new Component(y, YUVImageI.Y_COMP), new Component(u, YUVImageI.CB_COMP), new Component(v, YUVImageI.CR_COMP), samplingRatio);
    }
}
