package org.common.jay.startBusiness.service;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizer;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerListener;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerResponse;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

/**
 * @author Jay
 * @version V1.0
 * @Package com.yeweiyang.token.startBusiness
 * @date 2025/2/8 09:02
 * 模拟 AI 配音服务调用
 */
public class AIVoiceService {

    public static File generateVoice1(String text, String outputPath) throws IOException {
        // 这里应该调用实际的 AI 配音 API，这里只是模拟生成一个空的音频文件
        File voiceFile = new File(outputPath);
        Files.createFile(voiceFile.toPath());
        System.out.println("Generated voice file: " + voiceFile.getAbsolutePath());
        return voiceFile;
    }

    public static final String ACCESS_KEY_ID = "uSRgNo7JKuiwH1xM";
    public static final String ACCESS_KEY_SECRET = "uSRgNo7JKuiwH1xM";

    public static void generateVoice(String text, String outputPath) throws Exception {

        AccessToken accessToken = new AccessToken(System.getenv().get("ALIYUN_AK_ID"), System.getenv().get("ALIYUN_AK_SECRET"));
        accessToken.apply();
        String token = accessToken.getToken();
        long expireTime = accessToken.getExpireTime();

        //AccessToken accessToken = new AccessToken(ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        NlsClient client = new NlsClient("wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1", accessToken.getToken());


        SpeechSynthesizerListener listener = new SpeechSynthesizerListener() {
            //接收语音合成的语音二进制数据
            @Override
            public void onMessage(ByteBuffer message) {
                // 在这里实现细节
            }

            // 语音合成结束
            @Override
            public void onComplete(SpeechSynthesizerResponse response) {
                // 在这里实现细节
            }

            @Override
            public void onFail(SpeechSynthesizerResponse speechSynthesizerResponse) {

            }
        };

        //创建实例，建立连接。
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(client, listener);

        synthesizer.setAppKey("uSRgNo7JKuiwH1xM");


        //设置返回音频的编码格式
        synthesizer.setFormat(OutputFormatEnum.WAV);
        //设置返回音频的采样率
        synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
        //发音人
        synthesizer.setVoice("zhistella");
        //语调，范围是-500~500，可选，默认是0。
        synthesizer.setPitchRate(100);
        //语速，范围是-500~500，默认是0。
        synthesizer.setSpeechRate(100);
        //设置用于语音合成的文本
        synthesizer.setText("欢迎使用智能语音合成服务，您可以说北京明天天气怎么样啊");
        // 是否开启字级别时间戳，默认不开启，需要注意并非所有发音人都支持该参数。
        synthesizer.addCustomedParam("enable_subtitle", false);

        synthesizer.start();

        //等待语音合成结束
        synthesizer.waitForComplete();
    }

}
