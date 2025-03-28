package com.common.jay.startBusiness.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version V1.0
 * @Package com.yeweiyang.token.utils
 * @date 2025/2/8 09:04
 * 自动化工具类
 */
public class VideoGeneratorTool {
    public static void main(String[] args) {
        // 批量生成的数量
        int batchSize = 5;
        // 图片和文本资源目录
        String imageDirectory = "path/to/image/directory";
        String textDirectory = "path/to/text/directory";
        // 输出目录
        String outputDirectory = "path/to/output/directory";

        try {
            // 获取图片和文本文件列表
            List<File> imageFiles = getFilesFromDirectory(imageDirectory);
            List<File> textFiles = getFilesFromDirectory(textDirectory);

            // 批量生成视频
            for (int i = 0; i < batchSize; i++) {
                // 随机选择图片和文本文件
                File imageFile = imageFiles.get(i % imageFiles.size());
                File textFile = textFiles.get(i % textFiles.size());

                // 读取文本内容
                String text = new String(Files.readAllBytes(textFile.toPath()));

                // 生成语音文件
                String voiceOutputPath = outputDirectory + "/voice_" + i + ".mp3";
                //@TODO File voiceFile = AIVoiceService.generateVoice(text, voiceOutputPath);

                // 合成视频
                String videoOutputPath = outputDirectory + "/video_" + i + ".mp4";
                //VideoSynthesisService.synthesizeVideo(imageFile, voiceFile, videoOutputPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 获取指定目录下的所有文件
    private static List<File> getFilesFromDirectory(String directoryPath) {
        List<File> files = new ArrayList<>();
        Path path = Paths.get(directoryPath);
        if (Files.exists(path) && Files.isDirectory(path)) {
            File directory = path.toFile();
            File[] fileArray = directory.listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    if (file.isFile()) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }
}
