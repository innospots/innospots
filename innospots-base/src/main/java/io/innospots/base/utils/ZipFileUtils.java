/*
 *  Copyright © 2021-2023 Innospots (http://www.innospots.com)
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.innospots.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ZipFileUtils
 *
 * @author Wren
 * @date 2022/8/7-16:51
 */
public class ZipFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(ZipFileUtils.class);

    private static Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * 这里path指的是读取的zip文件路径
     *
     * @param path
     * @return
     */
    public static List<String> getFileName(String path) {
        return getFileName(path, UTF_8);
    }

    /**
     * 这里path指的是读取的zip文件路径  指定路径字符集
     *
     * @param path
     * @param charset
     * @return
     */
    public static List<String> getFileName(String path, Charset charset) {
        List<String> fileNames = new ArrayList<>();
        try {
            //这里一定要带入格式，不是在读取zip文件的时候会存在问题
            ZipFile zipFile = new ZipFile(path, charset != null ? charset : UTF_8);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String fileName = entries.nextElement().getName();
                fileNames.add(fileName);
            }
        } catch (Exception e) {
            logger.error("get zip file names error", e);
        }
        return fileNames;
    }

    public static List<String> unZip(String zipPath, String resourcePath) throws IOException {
        return unZip(zipPath, resourcePath, UTF_8);
    }

    public static List<String> unZip(String zipPath, String resourcePath, Charset charset) throws IOException {
        List<String> fileNames = new ArrayList<>();
        //判断生成目录是否生成，如果没有就创建
        File pathFile = new File(resourcePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zp = null;
        //指定编码，否则压缩包里面不能有中文目录
        zp = new ZipFile(zipPath, charset != null ? charset : UTF_8);
        //遍历里面的文件及文件夹
        Enumeration entries = zp.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zp.getInputStream(entry);
            String outPath = (resourcePath + File.separator + zipEntryName).replace("/", File.separator);
            //判断路径是否存在，不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            OutputStream out = new FileOutputStream(outPath);
            byte[] bf = new byte[2048];
            int len;
            while ((len = in.read(bf)) > 0) {
                out.write(bf, 0, len);
            }
            in.close();
            out.close();
            fileNames.add(zipEntryName);
        }
        zp.close();

        return fileNames;
    }

    //压缩文件
    public static void zipPark(String inputFile, String outputFile) throws IOException {
        //创建zip输出流
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
        //创建缓存输出流
        BufferedOutputStream bos = new BufferedOutputStream(out);
        File input = new File(inputFile);
        compress(out, bos, input, null);
        bos.close();
        out.close();//要注意关闭流，不是会导致最终结果出现问题
    }

    public static void compress(ZipOutputStream out, BufferedOutputStream bos, File input, String name) throws IOException {
        if (name == null) {
            name = input.getName();
        }
        //如果输入的文件名称为文件夹,需要遍历里面的文件及文件夹下文件遍历;如果是文件就只需要将该文件进行压缩
        if (input.isDirectory()) {
            File[] files = input.listFiles();

            if (files.length == 0) {//当该文件夹为空时,只需要将该目录存入压缩文件中即可
                out.putNextEntry(new ZipEntry(name + "/"));
            } else {
                for (int i = 0; i < files.length; i++) {
                    compress(out, bos, files[i], name + "/" + files[i].getName());
                }
            }
        } else {
            out.putNextEntry(new ZipEntry(name));
            FileInputStream fos = new FileInputStream(input);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int len;
            byte[] bf = new byte[1024];
            while ((len = bis.read(bf)) != -1) {
                bos.write(bf, 0, len);
                bos.flush();
            }
            bis.close();
            fos.close();
        }
    }
}
