package com.igeeksky.xcache.autoconfigure;

import com.igeeksky.xcache.config.props.CacheProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-29
 */
@Configuration
@ConfigurationProperties(prefix = "xcache")
public class CacheProperties {

    private String application;
    private CacheProps t0;
    private CacheProps t1;
    private CacheProps t2;
    private CacheProps t3;
    private CacheProps t4;
    private CacheProps t5;
    private CacheProps t6;
    private CacheProps t7;
    private CacheProps t8;
    private CacheProps t9;

    private List<CacheProps> caches;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public CacheProps getT0() {
        return t0;
    }

    public void setT0(CacheProps t0) {
        this.t0 = t0;
    }

    public CacheProps getT1() {
        return t1;
    }

    public void setT1(CacheProps t1) {
        this.t1 = t1;
    }

    public CacheProps getT2() {
        return t2;
    }

    public void setT2(CacheProps t2) {
        this.t2 = t2;
    }

    public CacheProps getT3() {
        return t3;
    }

    public void setT3(CacheProps t3) {
        this.t3 = t3;
    }

    public CacheProps getT4() {
        return t4;
    }

    public void setT4(CacheProps t4) {
        this.t4 = t4;
    }

    public CacheProps getT5() {
        return t5;
    }

    public void setT5(CacheProps t5) {
        this.t5 = t5;
    }

    public CacheProps getT6() {
        return t6;
    }

    public void setT6(CacheProps t6) {
        this.t6 = t6;
    }

    public CacheProps getT7() {
        return t7;
    }

    public void setT7(CacheProps t7) {
        this.t7 = t7;
    }

    public CacheProps getT8() {
        return t8;
    }

    public void setT8(CacheProps t8) {
        this.t8 = t8;
    }

    public CacheProps getT9() {
        return t9;
    }

    public void setT9(CacheProps t9) {
        this.t9 = t9;
    }

    public List<CacheProps> getCaches() {
        return caches;
    }

    public void setCaches(List<CacheProps> caches) {
        this.caches = caches;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{", "}");

        joiner.add("\"application\":\"" + application + "\"");
        if (t0 != null) joiner.add("\"t0\":" + t0);
        if (t1 != null) joiner.add("\"t1\":" + t1);
        if (t2 != null) joiner.add("\"t2\":" + t2);
        if (t3 != null) joiner.add("\"t3\":" + t3);
        if (t4 != null) joiner.add("\"t4\":" + t4);
        if (t5 != null) joiner.add("\"t5\":" + t5);
        if (t6 != null) joiner.add("\"t6\":" + t6);
        if (t7 != null) joiner.add("\"t7\":" + t7);
        if (t8 != null) joiner.add("\"t8\":" + t8);
        if (t9 != null) joiner.add("\"t9\":" + t9);
        if (caches != null) joiner.add("caches=" + caches);

        return joiner.toString();
    }
}
