package com.common.jay.startBusiness.pojo;

import lombok.Data;

/**
 * @author Jay
 * @version V1.0
 * @Package com.yeweiyang.token.startBusiness.pojo
 * @date 2025/2/8 10:31
 */
@Data
//@TableName(value = "testpoi")
public class VideoMaterialDO {

//    @TableId(type = IdType.AUTO)
    private Long id;
    private String type; // 素材类型，如图片、文本
    private String filePath; // 素材文件路径
}
