package com.cybersoft.common;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;


public class ImageVerifyItem {
	public byte[] image;
	public String randNum;
	public String Message;
    /**
     *  根據長、寬以及驗證碼的個數生成圖片驗證碼物件
     *  @param width
     *  @param height
     *  @param randNo
     *  @return
     * */
    public boolean creatImageVerify(int width, int height, int randNo) {
    	
    	
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 獲取圖形上下文
        Graphics graphics = image.getGraphics();
        // 生成隨機類
        Random random = new Random();
        // 設定背景色
        graphics.setColor(getRandColor(200, 250));
        // 填充指定的矩形
        graphics.fillRect(0, 0, width, height);
        // 設定字體
        graphics.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        // 畫邊框
        // graphics.setColor(new Color());
        // graphics.drawRect(0, 0, width-1, height-1);
        // 隨機產生155條干擾線，使圖像中的認證碼不易被其他程式探測到
        graphics.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            graphics.drawLine(x, y, xl, yl);
        }
        // 取隨機產生的驗證碼
        String sRand = "";
        for (int i = 0; i < randNo; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            // 將驗證碼顯示到圖像中
            graphics.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            graphics.drawString(rand, 13*i +6, 16);
        }
        // 釋放此圖形的上下文以及它使用的所有系統資源。
        graphics.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", baos);
        } catch (IOException e) {
            e.printStackTrace();
            this.Message = e.getMessage();
            return false;
        }
        this.image = baos.toByteArray();
        this.randNum = sRand;
        return true;
    }

    /**
     * 根據給定範圍獲得隨機顏色
     *
     * */
    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}
