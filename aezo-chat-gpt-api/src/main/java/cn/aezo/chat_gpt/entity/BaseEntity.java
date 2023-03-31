package cn.aezo.chat_gpt.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 公共属性，需注意
 * @author smalle
 * @since 2020-12-14 13:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode
public class BaseEntity implements Serializable {
    /**
     * ID<br/>
     * IdType.ASSIGN_ID: 该策略会使用雪花算法自动生成主键ID，主键类型为长或字符串（分别对应的MySQL的表字段为BIGINT和VARCHAR）<br/>
     * IdType.INPUT: 手动赋值
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 组织ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String partyId;

    /**
     * 有效状态. 1:有效; 0:无效
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer validStatus;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(exist = false)
    private String creatorName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(exist = false)
    private String updaterName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;

}
