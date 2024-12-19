package com.anran.cloudlibrary.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin");

    private final String name;
    private final String value;

    UserRoleEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据 枚举值的value 去获得枚举值
     * @param value 枚举值 value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if(ObjUtil.isEmpty(value)) {
            return null;
        }
        // 遍历所有的枚举值 是否存在该 枚举值value
        for(UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if(userRoleEnum.value.equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
