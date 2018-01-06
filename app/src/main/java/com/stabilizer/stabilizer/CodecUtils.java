package com.stabilizer.stabilizer;

import android.media.Image;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by sharganov on 06.01.18.
 */

public class CodecUtils  {
    private static final String TAG = "CodecUtils";

    static {
        Log.i(TAG, "before loadlibrary");
        // System.loadLibrary("codec-utils-jni");
        System.loadLibrary("native-lib");

        Log.i(TAG, "after loadlibrary");
    }

    private static class ImageWrapper extends CodecImage {
        private final Image mImage;
        private final Plane[] mPlanes;

        private ImageWrapper(Image image) {
            mImage = image;
            Image.Plane[] planes = mImage.getPlanes();

            mPlanes = new Plane[planes.length];
            for (int i = 0; i < planes.length; i++) {
                mPlanes[i] = new PlaneWrapper(planes[i]);
            }
        }

        public static ImageWrapper createFromImage(Image image) {
            return new ImageWrapper(image);
        }

        @Override
        public int getFormat() {
            return mImage.getFormat();
        }

        @Override
        public int getWidth() {
            return mImage.getWidth();
        }

        @Override
        public int getHeight() {
            return mImage.getHeight();
        }

        @Override
        public long getTimestamp() {
            return mImage.getTimestamp();
        }

        @Override
        public Plane[] getPlanes() {
            return mPlanes;
        }

        @Override
        public void close() {
            mImage.close();
        }

        private static class PlaneWrapper extends CodecImage.Plane {
            private final Image.Plane mPlane;

            PlaneWrapper(Image.Plane plane) {
                mPlane = plane;
            }

            @Override
            public int getRowStride() {
                return mPlane.getRowStride();
            }

            @Override
            public int getPixelStride() {
                return mPlane.getPixelStride();
            }

            @Override
            public ByteBuffer getBuffer() {
                return mPlane.getBuffer();
            }
        }
    }

    public native static void copyFlexYUVImage(CodecImage target, CodecImage source);

    public native static void matToImage(long addr, CodecImage dst);

    public static void copyMatToImage(long addr, Image dst)
    {
        matToImage(addr, ImageWrapper.createFromImage(dst));
    }

    public static void copyFlexYUVImage(Image target, Image source) {
        copyFlexYUVImage(
                ImageWrapper.createFromImage(target),
                ImageWrapper.createFromImage(source));
    }
}