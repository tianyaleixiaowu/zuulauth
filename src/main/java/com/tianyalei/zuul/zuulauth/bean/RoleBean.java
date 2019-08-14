package com.tianyalei.zuul.zuulauth.bean;

import java.util.Objects;

/**
 * @author wuweifeng wrote on 2019-08-13.
 */
public class RoleBean {
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleBean roleBean = (RoleBean) o;
        return Objects.equals(role, roleBean.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
