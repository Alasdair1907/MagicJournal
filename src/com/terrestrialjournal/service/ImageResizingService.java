package com.terrestrialjournal.service;
/*
  User: Alasdair
  Date: 1/1/2020
  Time: 6:15 PM                                                                                                    
                                        `.------:::--...``.`                                        
                                    `-:+hmmoo+++dNNmo-.``/dh+...                                    
                                   .+/+mNmyo++/+hmmdo-.``.odmo -/`                                  
                                 `-//+ooooo++///////:---..``.````-``                                
                           `````.----:::/::::::::::::--------.....--..`````                         
           ```````````...............---:::-----::::---..------------------........```````          
        `:/+ooooooosssssssyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysssssssssssssssssssssssssssoo+/:`       
          ``..-:/++ossyhhddddddddmmmmmarea51mbobmlazarmmmmmmmddddddddddddddhhyysoo+//:-..``         
                      ```..--:/+oyhddddmmmmmmmmmmmmmmmmmmmmmmmddddys+/::-..````                     
                                 ``.:oshddmmmmmNNNNNNNNNNNmmmhs+:.`                                 
                                       `.-/+oossssyysssoo+/-.`                                      
                                                                                                   
*/

import com.terrestrialjournal.util.Tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.imgscalr.Scalr;

public class ImageResizingService {

    public static Double resize(String inputFileName, String outputFileName, Integer maxWidthForHorizontal, Integer maxHeightForVertical){
        try {
            File inputFile = new File(inputFileName);
            BufferedImage inputImage = ImageIO.read(inputFile);

            Integer x = inputImage.getWidth();
            Integer y = inputImage.getHeight();

            Double aspectRatio = (double) x / (double) y;

            Integer newX = null;
            Integer newY = null;

            Double ratio = null;

            if (x > y){
                // horizontal

                if (maxWidthForHorizontal >= x){
                    copy(inputFileName, outputFileName);
                    return aspectRatio;
                }

                ratio = ( x.doubleValue() / maxWidthForHorizontal.doubleValue());

                newX = maxWidthForHorizontal;
                newY = ((Double) (y.doubleValue() / ratio)).intValue();

            } else {
                // vertical or square

                if (maxHeightForVertical >= y){
                    copy(inputFileName, outputFileName);
                    return aspectRatio;
                }

                ratio = y.doubleValue() / maxHeightForVertical.doubleValue();

                newX = ((Double) (x.doubleValue() / ratio)).intValue();
                newY = maxHeightForVertical;
            }

            BufferedImage outputImage = Scalr.resize(inputImage, Scalr.Method.ULTRA_QUALITY, newX, newY);
            ImageIO.write(outputImage, Tools.getExtension(inputFileName), new File(outputFileName));

            return aspectRatio;

        } catch (Exception ex){
            Tools.log("resize() error: "+ex.getMessage());
        }

        return null;
    }

    private static void copy(String inputFileName, String outputFileName){
        try {
            Files.copy(Paths.get(inputFileName), Paths.get(outputFileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex){
            Tools.log("[ERROR] error copying file "+inputFileName+" to "+outputFileName+":\r\n"+ex.getMessage());
        }
    }
}
