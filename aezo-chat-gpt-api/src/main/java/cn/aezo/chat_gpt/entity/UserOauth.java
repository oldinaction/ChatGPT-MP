package cn.aezo.chat_gpt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 用户第三方登录信息
 * </p>
 *
 * @author smalle
 * @since 2020-11-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@ToString(callSuper = true)
@TableName(value = "pt_user_oauth", excludeProperty = {"partyId"})
public class UserOauth extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 登录类型. WeChat/Alipay
     */
    private String loginType;

    /**
     * 昵称
     */
    private String nickName;

    private String openid;
}
