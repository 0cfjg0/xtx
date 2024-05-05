package com.itheima.xiaotuxian.client.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PagePayResponse.java
 * @Description TODO
 */
@Data
@NoArgsConstructor
public class WapPayResponse extends BasicResponse {

    @JSONField(name="prepay_id")
    private String prepayId;

}
