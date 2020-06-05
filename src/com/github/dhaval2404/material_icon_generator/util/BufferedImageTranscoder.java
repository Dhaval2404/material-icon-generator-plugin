package com.github.dhaval2404.material_icon_generator.util;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;

/**
 * Get BufferImage from SVG File
 *
 * <p>
 * This file was modified by the author from the following original source code:
 * https://stackoverflow.com/questions/20664107/draw-svg-images-on-a-jpanel#answer-20664243
 * <p>
 * Created by Dhaval Patel on 05 June 2020.
 */
public class BufferedImageTranscoder extends ImageTranscoder {

    private BufferedImage img = null;

    public BufferedImageTranscoder(String svgUri, float size) {
        setSize(size);

        TranscoderInput input = new TranscoderInput(svgUri);
        try {
            transcode(input, null);
        } catch (TranscoderException e) {
            e.printStackTrace();
        }
    }

    private void setSize(float size) {
        addTranscodingHint(PNGTranscoder.KEY_WIDTH, size);
        addTranscodingHint(PNGTranscoder.KEY_HEIGHT, size);
    }

    @Override
    public BufferedImage createImage(int w, int h) {
        return getEmptyBufferedImage(w, h);
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output) {
        this.img = img;
    }

    public BufferedImage getBufferedImage() {
        return img;
    }

    public static BufferedImage getEmptyBufferedImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

}