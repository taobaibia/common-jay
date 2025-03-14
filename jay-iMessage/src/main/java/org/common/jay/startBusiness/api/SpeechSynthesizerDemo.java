package org.common.jay.startBusiness.api;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizer;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerListener;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
/**
 * 此示例演示了：
 *      语音合成API调用。
 *      动态获取token。获取Token具体操作，请参见：https://help.aliyun.com/document_detail/450514.html
 *      流式合成TTS。
 *      首包延迟计算。
 */
public class SpeechSynthesizerDemo {

    public static final String ACCESS_KEY_ID = "LTAI5t7TprD7PiMGx8kq5tTq";
    public static final String ACCESS_KEY_SECRET = "xjHQIRXx7EGatoaNm0VDtidGDfyICU";
    public static final String APPKEY = "uSRgNo7JKuiwH1xM";
    private static final Logger logger = LoggerFactory.getLogger(SpeechSynthesizerDemo.class);
    private static long startTime;
    private String appKey;
    NlsClient client;
    public SpeechSynthesizerDemo(String appKey, String token, String url) {
        this.appKey = appKey;
        //TODO 重要提示 创建NlsClient实例,应用全局创建一个即可,生命周期可和整个应用保持一致,默认服务地址为阿里云线上服务地址
        if(url.isEmpty()) {
            client = new NlsClient(token);
        }else {
            client = new NlsClient(url, token);
        }
    }
    public SpeechSynthesizerDemo(String appKey, String accessKeyId, String accessKeySecret, String url) {
        this.appKey = appKey;
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            System.out.println("get token: " + accessToken.getToken() + ", expire time: " + accessToken.getExpireTime());
            if(url.isEmpty()) {
                client = new NlsClient(accessToken.getToken());
            }else {
                client = new NlsClient(url, accessToken.getToken());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static SpeechSynthesizerListener getSynthesizerListener() {
        SpeechSynthesizerListener listener = null;
        try {
            listener = new SpeechSynthesizerListener() {
                File fileName =new File("/Users/jay/Desktop/startBusiness/VideoDemo/申智敏你听.wav");
                //File f=new File("tts_test.wav");
                FileOutputStream fout = new FileOutputStream(fileName);
                private boolean firstRecvBinary = true;
                //语音合成结束
                @Override
                public void onComplete(SpeechSynthesizerResponse response) {
                    //调用onComplete时表示所有TTS数据已接收完成，因此为整个合成数据的延迟。该延迟可能较大，不一定满足实时场景。
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus()+
                            ", output file :"+fileName.getAbsolutePath()
                    );
                }
                //语音合成的语音二进制数据
                @Override
                public void onMessage(ByteBuffer message) {
                    try {
                        if(firstRecvBinary) {
                            //计算首包语音流的延迟，收到第一包语音流时，即可以进行语音播放，以提升响应速度（特别是实时交互场景下）。
                            firstRecvBinary = false;
                            long now = System.currentTimeMillis();
                            logger.info("tts first latency : " + (now - SpeechSynthesizerDemo.startTime) + " ms");
                        }
                        byte[] bytesArray = new byte[message.remaining()];
                        message.get(bytesArray, 0, bytesArray.length);
                        fout.write(bytesArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFail(SpeechSynthesizerResponse response){
                    //task_id是调用方和服务端通信的唯一标识，当遇到问题时需要提供task_id以便排查。
                    System.out.println(
                            "task_id: " + response.getTaskId() +
                                    //状态码 20000000 表示识别成功
                                    ", status: " + response.getStatus() +
                                    //错误信息
                                    ", status_text: " + response.getStatusText());
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listener;
    }
    public void process() {
        SpeechSynthesizer synthesizer = null;
        try {
            //创建实例，建立连接。
            synthesizer = new SpeechSynthesizer(client, getSynthesizerListener());
            synthesizer.setAppKey(appKey);
            //设置返回音频的编码格式
            synthesizer.setFormat(OutputFormatEnum.WAV);
            //设置返回音频的采样率
            synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            //发音人
            //synthesizer.setVoice("zhistella");
            synthesizer.setVoice("zhimiao_emo");


            //语调，范围是-500~500，可选，默认是0。
            synthesizer.setPitchRate(0);
            //语速，范围是-500~500，默认是0。
            synthesizer.setSpeechRate(0);
            //设置用于语音合成的文本
            //synthesizer.setText("你们好啊，我亲爱的学斯 我亲爱的艾伦 今天是北京时间2025年2月8日");
            synthesizer.setText("你好，是***老师吗？我是妙瓦底教育局的王主任，有个重要通知。最近教育部针对初中心理健康教育推行了全新的教材与培训体系，要求各学校心理老师必须参加线上培训并采购指定教材。这次培训直接和教师评级挂钩，关乎你未来的职业发展" +
                    "培训费用是3800元，教材费1200元，需要你尽快转账到指定账户，转账后把截图发给我，方便登记。为了证明身份，我给你发了教育局内部文件截图，你注意查收。对了，这次培训时间紧、任务重，要是错过，不仅评级受影响，还可能影响下学期的教学安排。你看你尽快把费用转一下，有任何问题随时联系我，我的电话一直畅通 。");
            // 是否开启字幕功能（返回相应文本的时间戳），默认不开启，需要注意并非所有发音人都支持该参数。
            synthesizer.addCustomedParam("enable_subtitle", false);
            //此方法将以上参数设置序列化为JSON格式发送给服务端，并等待服务端确认。
            long start = System.currentTimeMillis();
            synthesizer.start();
            logger.info("tts start latency " + (System.currentTimeMillis() - start) + " ms");
            SpeechSynthesizerDemo.startTime = System.currentTimeMillis();
            //等待语音合成结束
            synthesizer.waitForComplete();
            logger.info("tts stop latency " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (null != synthesizer) {
                synthesizer.close();
            }
        }
    }
    public void shutdown() {
        client.shutdown();
    }
    public static void main(String[] args) throws Exception {

        String appKey = "uSRgNo7JKuiwH1xM";
        String token = "3bb3984720cb45b3a75c3aaaafa151ac";
        String url = "wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1"; // 默认即可，默认值：wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1

        //SpeechSynthesizerDemo demo = new SpeechSynthesizerDemo(APPKEY, ACCESS_KEY_ID, ACCESS_KEY_SECRET, url);
        SpeechSynthesizerDemo demo = new SpeechSynthesizerDemo(appKey, token, url);

        demo.process();
        demo.shutdown();



    }
}