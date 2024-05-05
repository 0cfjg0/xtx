package com.itheima.xiaotuxian.vo.property;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author  lvbencai 2023年4月26日20:22:11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertySimpleVo {
    /**
     * id
     */
    private String id;
    /**
     * 属性名称
     */
    private String name;
    /**
     * 属性值
     */
    private String value;
}
