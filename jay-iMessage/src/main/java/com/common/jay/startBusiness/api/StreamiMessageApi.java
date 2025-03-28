package com.common.jay.startBusiness.api;

import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.StreamInputTts;
import com.alibaba.nls.client.protocol.tts.StreamInputTtsListener;
import com.alibaba.nls.client.protocol.tts.StreamInputTtsResponse;
import com.common.jay.startBusiness.service.PlaybackRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Jay
 * @version V1.0
 * @Package org.common.jay.startBusiness.api
 * @date 2025/3/14 16:44
 */
@Service
@Slf4j
public class StreamiMessageApi {


    private static long startTime;
    NlsClient client;

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
                        System.out.println("tts first latency : " + (now - StreamiMessageApi.startTime) + " ms");
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


    public void process(String[] textArray, String appKey,String token, String url) throws InterruptedException {
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
        if (url.isEmpty()) {
            client = new NlsClient(token);
        } else {
            client = new NlsClient(url, token);
        }
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
            synthesizer.setSpeechRate(-200);
            //此方法将以上参数设置序列化为JSON发送给服务端，并等待服务端确认。
            long start = System.currentTimeMillis();
            synthesizer.startStreamInputTts();
            System.out.println("tts start latency " + (System.currentTimeMillis() - start) + " ms");
            StreamiMessageApi.startTime = System.currentTimeMillis();
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


}
