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
     *  �ھڪ��B�e�H�����ҽX���Ӽƥͦ��Ϥ����ҽX����
     *  @param width
     *  @param height
     *  @param randNo
     *  @return
     * */
    public boolean creatImageVerify(int width, int height, int randNo) {
    	
    	
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // ����ϧΤW�U��
        Graphics graphics = image.getGraphics();
        // �ͦ��H����
        Random random = new Random();
        // �]�w�I����
        graphics.setColor(getRandColor(200, 250));
        // ��R���w���x��
        graphics.fillRect(0, 0, width, height);
        // �]�w�r��
        graphics.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        // �e���
        // graphics.setColor(new Color());
        // graphics.drawRect(0, 0, width-1, height-1);
        // �H������155���z�Z�u�A�ϹϹ������{�ҽX�����Q��L�{��������
        graphics.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            graphics.drawLine(x, y, xl, yl);
        }
        // ���H�����ͪ����ҽX
        String sRand = "";
        for (int i = 0; i < randNo; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            // �N���ҽX��ܨ�Ϲ���
            graphics.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            graphics.drawString(rand, 13*i +6, 16);
        }
        // ���񦹹ϧΪ��W�U��H�Υ��ϥΪ��Ҧ��t�θ귽�C
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
     * �ھڵ��w�d����o�H���C��
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
