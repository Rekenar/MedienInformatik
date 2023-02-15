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




        if (samplingRatio == 1) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j += 2) {
                    u[i][j+1] = u[i][j];
                    v[i][j+1] = v[i][j];
                }
            }
        }else if (samplingRatio == 2){
            for (int i = 0; i < height; i += 2) {
                for (int j = 0; j < width; j += 2) {
                    u[i][j+1] = u[i][j];
                    v[i][j+1] = v[i][j];
                    u[i+1][j] = u[i][j];
                    v[i+1][j] = v[i][j];
                    u[i+1][j+1] = u[i][j];
                    v[i+1][j+1] = v[i][j];
                }
            }
        }
        return new YUVImage(new Component(y, YUVImageI.Y_COMP), new Component(u, YUVImageI.CB_COMP), new Component(v, YUVImageI.CR_COMP), samplingRatio);
    }

}
