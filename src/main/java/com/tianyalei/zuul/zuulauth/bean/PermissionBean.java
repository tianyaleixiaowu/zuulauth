package com.tianyalei.zuul.zuulauth.bean;

import java.util.Objects;

/**
 * @author wuweifeng wrote on 2019/8/12.
 */
public class PermissionBean {
    /**
     * 权限标识（"role:get","contact:post","project:*",冒号前面是请求地址，冒号后面是请求方式，*号代表通配增删改查都行。）
     */
    private String code;

    public PermissionBean(String code) {
        this.code = code;
    }

    public PermissionBean() {
    }

    @Override
    public String toString() {
        return "PermissonBean{" +
                "code='" + code + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionBean that = (PermissionBean) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
