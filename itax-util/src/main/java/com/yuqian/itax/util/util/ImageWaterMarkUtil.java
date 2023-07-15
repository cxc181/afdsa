package com.yuqian.itax.util.util;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ImageWaterMarkUtil {

    /**
     * 对原图增加图片水印，并返回图片数据字节
     *
     * @param watermarkBase64String 水印图base64编码
     * @param sourceImageUrl        原图Url
     * @param qrLeftMargin          二维码图片左边距
     * @param qrTopMargin           二维码图片上边距
     * @param qrWidth               二维码图片宽
     * @param qrHeight              二维码图片高
     * @param sourceType            操作小程序来源 1-微信小程序 2-支付宝小程序
     * @return 图片数据字节
     */
    public static byte[] addImageWatermark(String watermarkBase64String, String sourceImageUrl,
                                           Integer qrLeftMargin, Integer qrTopMargin,
                                           Integer qrWidth, Integer qrHeight, String sourceType) throws Exception {
        try {
            File sourceImageFile = new File(sourceImageUrl);
            URL url = new URL(sourceImageUrl);
            BufferedImage sourceImage = ImageIO.read(url);

            // base64转图片
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes2 = decoder.decodeBuffer(watermarkBase64String);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes2);
            BufferedImage watermarkImage = ImageIO.read(inputStream);

            // 初始化必要的图形属性
            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 取水印图大小
            Pair<Double, Double> watermarkSizePair = calculateWatermarkDimensions(watermarkImage, qrWidth, qrHeight, sourceType);

            // 如果是支付宝邀请码，修改二维码图片左边距
            if (Objects.equals("2", sourceType)) {
                qrLeftMargin = qrLeftMargin + (watermarkSizePair.getRight().intValue() - watermarkSizePair.getLeft().intValue()) / 2;
            }

            // 取起始点
            Pair<Integer, Integer> topPoint = Pair.of(qrLeftMargin, qrTopMargin);

            // 绘制图像水印
            g2d.drawImage(resizeImage(watermarkImage,
                    watermarkSizePair.getLeft().intValue(), watermarkSizePair.getRight().intValue()),
                    topPoint.getRight(), topPoint.getLeft(), null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String ext = sourceImageFile.getName().substring(sourceImageFile.getName().lastIndexOf('.') + 1);
            ImageIO.write(sourceImage, ext, baos);
            g2d.dispose();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new Exception("无法将图像水印添加到图像", e);
        }
    }

    /**
     * 将图像大小调整为绝对宽度和高度（图像可能不成比例）
     *
     * @param sourceImage  原图文件
     * @param scaledWidth  绝对宽度（像素）
     * @param scaledHeight 绝对高度（像素）
     * @return 输出图像
     */
    public static BufferedImage resizeImage(BufferedImage sourceImage, int scaledWidth, int scaledHeight) {
        // 创建输出图像
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, sourceImage.getType());

        // 将输入图像缩放到输出图像
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(sourceImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return outputImage;
    }

    /**
     * 计算水印的大小
     *
     * @param watermarkImage     水印图
     * @param maxWatermarkWidth  水印框宽度
     * @param maxWatermarkHeight 水印框高度
     * @param sourceType         操作小程序来源 1-微信小程序 2-支付宝小程序 4-字节跳动
     * @return 水印的大小
     */
    private static Pair<Double, Double> calculateWatermarkDimensions(
            BufferedImage watermarkImage, double maxWatermarkWidth,
            double maxWatermarkHeight, String sourceType) {

        double watermarkWidth = watermarkImage.getWidth();
        double watermarkHeight = watermarkImage.getHeight();

        if (Objects.equals("1", sourceType)) {
            if (watermarkWidth > maxWatermarkWidth) {
                double aspectRatio = watermarkWidth / watermarkHeight;
                watermarkWidth = maxWatermarkWidth;
                watermarkHeight = watermarkWidth / aspectRatio;
            }

            if (watermarkHeight > maxWatermarkHeight) {
                double aspectRatio = watermarkWidth / watermarkHeight;
                watermarkHeight = maxWatermarkHeight;
                watermarkWidth = watermarkHeight / aspectRatio;
            }
        } else if (Objects.equals("2", sourceType)) {
            double aspectRatio = watermarkWidth / watermarkHeight;
            watermarkWidth = maxWatermarkHeight * aspectRatio;
            watermarkHeight = maxWatermarkHeight;
        }else if (Objects.equals("4", sourceType)) {
            double aspectRatio = watermarkWidth / watermarkHeight;
            watermarkWidth = maxWatermarkHeight * aspectRatio;
            watermarkHeight = maxWatermarkHeight;
        }
        return Pair.of(watermarkHeight, watermarkWidth);
    }


    /**
     * 坐标点对
     *
     * @param <R>
     * @param <L>
     */
    public static class Pair<R, L> {
        private R right;
        private L left;

        private Pair(R right, L left) {
            this.right = right;
            this.left = left;
        }

        public static <R, L> Pair of(R right, L left) {
            return new Pair(right, left);
        }

        public R getRight() {
            return right;
        }

        public L getLeft() {
            return left;
        }
    }
}
