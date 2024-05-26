/*
 * MIT License
 *
 * Copyright (c) 2017 Pim van den Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vulkantechnologies.pnet.client.util;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.vulkantechnologies.pnet.client.Client;
import com.vulkantechnologies.pnet.event.PNetListener;
import com.vulkantechnologies.pnet.packet.Packet;

import lombok.experimental.Delegate;

public class ClientDecorator implements Client {

    @Delegate
    protected final Client client;
    protected PNetListener clientListener;

    public ClientDecorator(final Client client) {
        this.client = client;

        client.setClientListener(new PNetListener() {
            @Override
            public void onConnect(final @NotNull Client c) {
                if (clientListener != null)
                    clientListener.onConnect(ClientDecorator.this);
            }

            @Override
            public void onDisconnect(final @NotNull Client c) {
                if (clientListener != null)
                    clientListener.onDisconnect(ClientDecorator.this);
            }

            @Override
            public void onReceive(final @NotNull Packet p, final @NotNull Client c) throws IOException {
                if (clientListener != null)
                    clientListener.onReceive(p, ClientDecorator.this);
            }
        });
    }
}
