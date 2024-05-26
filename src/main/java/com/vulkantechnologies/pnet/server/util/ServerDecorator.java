package com.vulkantechnologies.pnet.server.util;

import com.vulkantechnologies.pnet.server.Server;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class ServerDecorator implements Server {

    @Delegate
    protected final Server server;
}
