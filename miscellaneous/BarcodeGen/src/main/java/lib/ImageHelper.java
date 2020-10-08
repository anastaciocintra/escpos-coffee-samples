/*
MIT License

Copyright (c) 2020 Marco Antonio Anastacio Cintra <anastaciocintra@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package lib;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.CoffeeImage;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.escpos.image.ImageWrapperInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Process big images end send to the printer.
 * The image is sliced into small pieces then send to the printer
 * When to use: If your image overflow the buffer of the printer, the output can be unpredictable.
 * Beta version 
 *
 */
public class ImageHelper {
    final int maxWidth;
    final int maxHeight;

    /**
     * creates an ImageHelper with default values
     *
     */
    public ImageHelper(){
        this(576,48);
    }

    /**
     * create an ImageHelper
     *
     * @param maxWidth read your printer documentation to discover the width max dots
     * @param maxHeight test / read your printer to discover the printer buffer size, this number should be as bigger as possible
     */
    public ImageHelper(int maxWidth, int maxHeight){
        //maxHeight need to be multiple of 24
        if(maxHeight < 24) maxHeight = 24;
        if((maxHeight % 24) != 0) maxHeight -= (maxHeight % 24);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    /**
     * Slice vertically the image in maxHeight offsets
     * @param coffeeImage implementation of CoffeeImage {@link java.awt.image.BufferedImage} or Bitmap (android)
     * @return a list (sliced) of the CoffeeImage
     */
    public List<CoffeeImage> sliceImage(CoffeeImage coffeeImage){
        List<CoffeeImage> listImages = new ArrayList<>();

        int x = 0;
        int y = 0;
        int x_offset = maxWidth;
        int y_offset = maxHeight;

        while (true) {
            // safety to not run in out of bound
            if(x > (coffeeImage.getWidth()-1)) {
                x = coffeeImage.getWidth()-1;
            }
            if((x+x_offset) > coffeeImage.getWidth()) {
                x_offset = coffeeImage.getWidth()- x;
            }

            if(y >= (coffeeImage.getHeight()-1)) {
                y = coffeeImage.getHeight()-1;
            }
            if((y+y_offset) > coffeeImage.getHeight()) {
                y_offset = coffeeImage.getHeight() - y;
            }

            CoffeeImage tmp = coffeeImage.getSubimage(0, y, x_offset, y_offset);
            listImages.add(tmp);

            y+=y_offset;
            if(y >= coffeeImage.getHeight()) break;
        }

        return listImages;
    }

    /**
     * just slice the image and print sequentially
     * with regular escpos write image
     * @param escPos
     * @param image
     * @param wrapper
     * @param bitonalAlgorithm
     * @throws IOException
     */
    public void write(EscPos escPos, CoffeeImage image, ImageWrapperInterface wrapper, Bitonal bitonalAlgorithm) throws IOException {
        List<CoffeeImage> images = sliceImage(image);
        for(CoffeeImage img : images){

            escPos.write(wrapper,new EscPosImage(img, bitonalAlgorithm));

        }
    }

}
