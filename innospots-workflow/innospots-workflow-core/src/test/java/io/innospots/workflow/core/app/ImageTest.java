package io.innospots.workflow.core.app;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author Smars
 * @date 2023/5/25
 */
public class ImageTest {

    @Test
    void test() throws IOException {
        BufferedImage image =  ImageIO.read(new File("/tmp/fff2.png"));
        System.out.println(image.getWidth()+","+image.getHeight());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("/tmp/11.png")));
        ImgUtil.scale(new FileInputStream(new File("/tmp/fff2.png")),bos,0.5f);
        bos.flush();
        bos.close();
        ImgUtil.convert(FileUtil.file("/tmp/11.png"),FileUtil.file("/tmp/11.jpg"));
        BufferedImage image2 =  ImgUtil.read("/tmp/11.jpg");
        System.out.println(image2.getWidth() +","+image2.getHeight());
    }
}
