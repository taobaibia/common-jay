package com.common.bops.iMessage.controller;

import com.common.bops.iMessage.dto.TextChatDTO;
import com.common.jay.startBusiness.api.StreamiMessageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jay
 * @version V1.0
 * @Package com.common.bops.iMessage
 * @date 2025/3/28 10:38
 */
//@Api(value = "理赔账户服务", tags = "理赔账户服务")
@RequestMapping("/api/v1/imessage/text")
@RestController
@Slf4j
public class iMessageController {

    @Value("${imessage.appKey}")
    private String appKey;
    @Value("${imessage.token}")
    private String token;
    @Value("${imessage.url}")
    private String url;

    @Autowired
    private StreamiMessageApi streamiMessageApi;


//    @ApiOperation(value = "测试", notes = "测试")
    @PostMapping("/chat")
    public void chat(@RequestBody TextChatDTO textChatDTO) {
        if (ObjectUtils.isEmpty(textChatDTO)|| StringUtils.isEmpty(textChatDTO.getTextArray())){
            throw new RuntimeException("参数不能为空");
        }
        try {
            streamiMessageApi.process(textChatDTO.getTextArray(), appKey, token, url);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        streamiMessageApi.shutdown();
    }


}
