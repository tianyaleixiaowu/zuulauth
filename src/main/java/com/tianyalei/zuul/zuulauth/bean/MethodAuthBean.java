package com.tianyalei.zuul.zuulauth.bean;

import com.tianyalei.zuul.zuulauth.annotation.Logical;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wuweifeng wrote on 2019/8/12.
 */
public class MethodAuthBean {
    /**
     * get，post，put，delete.一个接口，可以有多个，譬如可以通知接收get和post
     */
    private Set<String> actions;
    /**
     * url=/role/{id}, url={/index, /""} ，可以是多个
     */
    private Set<String> urls = new HashSet<>();
    /**
     * 需要的角色，可以是多个
     */
    private Set<String> roles = new HashSet<>();
    /**
     * 角色之间的关系，and， or
     */
    private Logical rolesLogical;
    /**
     * 需要匹配的权限，如menu:add
     */
    private Set<String> codes = new HashSet<>();
    /**
     * 权限之间的关系，and， or
     */
    private Logical codesLogical;

    public Logical getRolesLogical() {
        return rolesLogical;
    }

    public void setRolesLogical(Logical rolesLogical) {
        this.rolesLogical = rolesLogical;
    }

    public Logical getCodesLogical() {
        return codesLogical;
    }

    public void setCodesLogical(Logical codesLogical) {
        this.codesLogical = codesLogical;
    }

    public Set<String> getCodes() {
        return codes;
    }

    public void setCodes(Set<String> codes) {
        this.codes = codes;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

}