package com.igeeksky.xcache.support.lettuce.config;

import com.igeeksky.xcache.config.HostAndPort;
import com.igeeksky.xcache.support.lettuce.config.props.ClientOptions;
import io.lettuce.core.ReadFrom;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class LettuceStandaloneConfig extends LettuceGenericConfig {

    private HostAndPort master = new HostAndPort("localhost", 6379);

    private ReadFrom readFrom = ReadFrom.UPSTREAM;

    private List<HostAndPort> replicas;

    private ClientOptions clientOptions;

    public HostAndPort getMaster() {
        return master;
    }

    public void setMaster(HostAndPort master) {
        this.master = master;
    }

    public ReadFrom getReadFrom() {
        return readFrom;
    }

    public void setReadFrom(ReadFrom readFrom) {
        this.readFrom = readFrom;
    }

    public List<HostAndPort> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<HostAndPort> replicas) {
        this.replicas = replicas;
    }

    public ClientOptions getClientOptions() {
        return clientOptions;
    }

    public void setClientOptions(ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

}