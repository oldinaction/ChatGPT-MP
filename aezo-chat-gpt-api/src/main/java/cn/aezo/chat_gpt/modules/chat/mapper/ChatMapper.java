package cn.aezo.chat_gpt.modules.chat.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

public interface ChatMapper {
    int updateUserAsset(@Param("userId") String userId, @Param("assetType") String assetType,
                        @Param("asset") BigDecimal asset, @Param("version") Integer version);
    int insertUserAssetHis(@Param("ctx") Map<String, Object> ctx);
}
