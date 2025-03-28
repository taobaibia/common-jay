package com.common.jay.startBusiness.api;

import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.StreamInputTts;
import com.alibaba.nls.client.protocol.tts.StreamInputTtsListener;
import com.alibaba.nls.client.protocol.tts.StreamInputTtsResponse;
import com.common.jay.startBusiness.service.PlaybackRunnable;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Jay
 * @version V1.0
 * @Package com.yeweiyang.token.startBusiness.service
 * @date 2025/2/11 14:56
 */
public class StreamInputTtsPlayableDemo {
    private static long startTime;
    NlsClient client;
    private String appKey;

    public StreamInputTtsPlayableDemo(String appKey, String token, String url) {
        this.appKey = appKey;
        //创建NlsClient实例应用全局创建一个即可。生命周期可和整个应用保持一致，默认服务地址为阿里云线上服务地址。
        if (url.isEmpty()) {
            client = new NlsClient(token);
        } else {
            client = new NlsClient(url, token);
        }
    }

    private static StreamInputTtsListener getSynthesizerListener(final PlaybackRunnable audioPlayer) {
        StreamInputTtsListener listener = null;
        try {
            listener = new StreamInputTtsListener() {

                File fileName =new File("/Users/jay/Desktop/startBusiness/VideoDemo/合成声音2.wav");
                //File f=new File("tts_test.wav");
                FileOutputStream fout = new FileOutputStream(fileName);
                private boolean firstRecvBinary = true;

                //流式文本语音合成开始
                @Override
                public void onSynthesisStart(StreamInputTtsResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus());
                }

                //服务端检测到了一句话的开始
                @Override
                public void onSentenceBegin(StreamInputTtsResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus());
                    System.out.println("Sentence Begin");
                }

                //服务端检测到了一句话的结束，获得这句话的起止位置和所有时间戳
                @Override
                public void onSentenceEnd(StreamInputTtsResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus() + ", subtitles: " + response.getObject("subtitles"));

                }

                //流式文本语音合成结束
                @Override
                public void onSynthesisComplete(StreamInputTtsResponse response) {
                    // 调用onSynthesisComplete时，表示所有TTS数据已经接收完成，所有文本都已经合成音频并返回。
                    System.out.println("name: " + response.getName() + ", status: " + response.getStatus());
                    audioPlayer.stop();
                }

                //收到语音合成的语音二进制数据
                @Override
                public void onAudioData(ByteBuffer message) {
                    if (firstRecvBinary) {
                        // 此处计算首包语音流的延迟，收到第一包语音流时，即可以进行语音播放，以提升响应速度（特别是实时交互场景下）。
                        firstRecvBinary = false;
                        long now = System.currentTimeMillis();
                        System.out.println("tts first latency : " + (now - StreamInputTtsPlayableDemo.startTime) + " ms");
                    }
                    byte[] bytesArray = new byte[message.remaining()];
                    message.get(bytesArray, 0, bytesArray.length);
                    System.out.println("recv audio bytes:" + bytesArray.length);
                    audioPlayer.put(ByteBuffer.wrap(bytesArray));
                    try {
                        fout.write(bytesArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                //收到语音合成的增量音频时间戳
                @Override
                public void onSentenceSynthesis(StreamInputTtsResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus() + ", subtitles: " + response.getObject("subtitles"));
                }

                @Override
                public void onFail(StreamInputTtsResponse response) {
                    // task_id是调用方和服务端通信的唯一标识，当遇到问题时，需要提供此task_id以便排查。
                    System.out.println(
                            "session_id: " + getStreamInputTts().getCurrentSessionId() +
                                    ", task_id: " + response.getTaskId() +
                                    //状态码
                                    ", status: " + response.getStatus() +
                                    //错误信息
                                    ", status_text: " + response.getStatusText());
                    audioPlayer.stop();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listener;
    }


    public void process(String[] textArray) throws InterruptedException {
        StreamInputTts synthesizer = null;
        PlaybackRunnable playbackRunnable = new PlaybackRunnable(24000);
        try {
            playbackRunnable.prepare();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        Thread playbackThread = new Thread(playbackRunnable);
        // 启动播放线程
        playbackThread.start();
        try {
            //创建实例，建立连接。
            synthesizer = new StreamInputTts(client, getSynthesizerListener(playbackRunnable));
            synthesizer.setAppKey(appKey);
            //设置返回音频的编码格式。
            synthesizer.setFormat(OutputFormatEnum.WAV);
            //设置返回音频的采样率。
            synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_24K);
//            synthesizer.setVoice("longxiaochun");温柔
//            synthesizer.setVoice("longyu");//御姐
//            synthesizer.setVoice("longyu");御姐
            synthesizer.setVoice("longxiaoxia");//温柔
            //音量，范围是0~100，可选，默认50。
            synthesizer.setVolume(50);
            //语调，范围是-500~500，可选，默认是0。
            synthesizer.setPitchRate(0);
            //语速，范围是-500~500，默认是0。
            synthesizer.setSpeechRate(-50);
            //此方法将以上参数设置序列化为JSON发送给服务端，并等待服务端确认。
            long start = System.currentTimeMillis();
            synthesizer.startStreamInputTts();
            System.out.println("tts start latency " + (System.currentTimeMillis() - start) + " ms");
            StreamInputTtsPlayableDemo.startTime = System.currentTimeMillis();
            //设置连续两次发送文本的最小时间间隔（毫秒），如果当前调用send时距离上次调用时间小于此值，则会阻塞并等待直到满足条件再发送文本
            synthesizer.setMinSendIntervalMS(100);
            for (String text : textArray) {
                //发送流式文本数据。
                synthesizer.sendStreamInputTts(text);
            }
            //通知服务端流式文本数据发送完毕，阻塞等待服务端处理完成。
            synthesizer.stopStreamInputTts();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (null != synthesizer) {
                synthesizer.close();
                playbackThread.join();
            }
        }
    }

    public void shutdown() {
        client.shutdown();
    }



    public static void main(String[] args) throws Exception {
        String appKey = "uSRgNo7JKuiwH1xM";
        String token = "3bb3984720cb45b3a75c3aaaafa151ac";
        // url取默认值
        String url = "wss://nls-gateway-cn-beijing.aliyuncs.com/ws/v1";
//        String[] textArray = {"流式文本语音合成SDK，", "可以将输入的文本", "合成为语音二进制数据，",
//                "相比于非流式语音合成，", "流式合成的优势在于实时性", "更强。用户在输入文本的同时",
//                "可以听到接近同步的语音输出，", "极大地提升了交互体验，", "减少了用户等待时间。",
//                "适用于调用大规模", "语言模型（LLM），以", "流式输入文本的方式", "进行语音合成的场景。","你好，是申智敏老师吗？我是妙瓦底教育局的王主任，有个重要通知。最近教育部针对初中心理健康教育推行了全新的教材与培训体系，要求各学校心理老师必须参加线上培训并采购指定教材。这次培训直接和教师评级挂钩，关乎你未来的职业发展\" +\n" +
//                "                    \"培训费用是3800元，教材费1200元，需要你尽快转账到指定账户，转账后把截图发给我，方便登记。为了证明身份，我给你发了教育局内部文件截图，你注意查收。对了，这次培训时间紧、任务重，要是错过，不仅评级受影响，还可能影响下学期的教学安排。你看你尽快把费用转一下，有任何问题随时联系我，我的电话一直畅通 。"};

//        String[] textArray = {"高跟鞋敲击地面，发出清脆声响。她双手环胸，红唇轻勾，眼神犀利，周身气场强大。“申智敏，少在我面前耍花样，没功夫陪你玩” 话音落下，周遭瞬间安静，无人敢再轻易造次"};
        String[] textArray = {"嗨～刘文博，吴学斯你们好吗，都市里的你，此刻是否也在夜色中忙碌或放松？欢迎来到今晚的你的月亮我的心" +"我是主持人申小杰 "+
                "华灯初上，城市被霓虹点亮。街头巷尾，车水马龙，忙碌了一天的人们，在这一刻有着不同的姿态。有人坐在公园长椅，静静看着夜空，放空思绪；有人穿梭在热闹的夜市，在烟火气中寻找慰藉" +
                "独自走在回家的路上，街边店铺的暖光透过窗户洒在地面。这座城市，给了我们追逐梦想的舞台，也有无人诉说的落寞。但每一盏亮着的灯，都像是一个希望的信号，提醒着我们，奋斗的日子里从不孤单" +
                "在这偌大的都市，我们都是追光者，怀揣着梦想前行。希望在这个夜晚，我的声音能陪伴你，感受这座城市的温度，继续勇敢地奔赴明天"};
        StreamInputTtsPlayableDemo demo = new StreamInputTtsPlayableDemo(appKey, token, url);
        demo.process(textArray);
        demo.shutdown();
    }
}