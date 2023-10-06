package com.igeeksky.xcache.support.lettuce.config.props;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class ClientResources {

    private Integer ioThreadPoolSize;
    private Integer computationThreadPoolSize;

    public Integer getIoThreadPoolSize() {
        return ioThreadPoolSize;
    }

    public void setIoThreadPoolSize(Integer ioThreadPoolSize) {
        this.ioThreadPoolSize = ioThreadPoolSize;
    }

    public Integer getComputationThreadPoolSize() {
        return computationThreadPoolSize;
    }

    public void setComputationThreadPoolSize(Integer computationThreadPoolSize) {
        this.computationThreadPoolSize = computationThreadPoolSize;
    }

}
