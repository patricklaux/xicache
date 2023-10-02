package com.igeeksky.xcache.config.props;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class CacheProps implements Cloneable {

    private String name;

    private String template;

    private String charset;

    private String cacheType;

    private LocalProps local = new LocalProps();

    private RemoteProps remote = new RemoteProps();

    private ExtensionProps extension = new ExtensionProps();

    private Map<String, Object> metadata = new HashMap<>();

    public CacheProps() {
    }

    public CacheProps(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public LocalProps getLocal() {
        return local;
    }

    public void setLocal(LocalProps local) {
        this.local = local;
    }

    public RemoteProps getRemote() {
        return remote;
    }

    public void setRemote(RemoteProps remote) {
        this.remote = remote;
    }

    public ExtensionProps getExtension() {
        return extension;
    }

    public void setExtension(ExtensionProps extension) {
        this.extension = extension;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public CacheProps clone() {
        try {
            CacheProps clone = (CacheProps) super.clone();
            clone.setLocal(this.local.clone());
            clone.setRemote(this.remote.clone());
            clone.setExtension(this.extension.clone());
            clone.setMetadata(new HashMap<>(this.metadata));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone operation is not support.", e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("name='" + name + "'")
                .add("template='" + template + "'")
                .add("charset='" + charset + "'")
                .add("cacheType='" + cacheType + "'")
                .add("local=" + local)
                .add("remote=" + remote)
                .add("extension=" + extension)
                .add("metadata=" + metadata)
                .toString();
    }
}
