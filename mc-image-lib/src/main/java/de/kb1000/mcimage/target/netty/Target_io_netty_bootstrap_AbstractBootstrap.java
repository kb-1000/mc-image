/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.netty;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

@TargetClass(AbstractBootstrap.class)
final class Target_io_netty_bootstrap_AbstractBootstrap {
    @Alias
    native Target_io_netty_bootstrap_AbstractBootstrap channelFactory(io.netty.bootstrap.ChannelFactory<? extends Channel> channelFactory);

    @Substitute
    Target_io_netty_bootstrap_AbstractBootstrap channel(Class<? extends Channel> channelClass) {
        // toString of the custom channel factories will be different but... it should work
        // This isn't pretty but can do without reflection
        if (channelClass == null) {
            throw new NullPointerException("channelClass");
        }
        if (channelClass == LocalServerChannel.class) {
            return channelFactory((ChannelFactory<? extends Channel>) LocalServerChannel::new);
        } else if (channelClass == LocalChannel.class) {
            return channelFactory((ChannelFactory<? extends Channel>) LocalChannel::new);
        } else if (channelClass == NioSocketChannel.class) {
            return channelFactory((ChannelFactory<? extends Channel>) NioSocketChannel::new);
        } else if (channelClass == NioServerSocketChannel.class) {
            return channelFactory((ChannelFactory<? extends Channel>) NioServerSocketChannel::new);
        } else if (channelClass == EpollSocketChannel.class && Epoll.isAvailable()) {
            return channelFactory((ChannelFactory<? extends Channel>) EpollSocketChannel::new);
        } else if (channelClass == EpollServerSocketChannel.class && Epoll.isAvailable()) {
            return channelFactory((ChannelFactory<? extends Channel>) EpollServerSocketChannel::new);
        }
        // will throw on newChannel
        // TODO: maybe throw here already?
        return channelFactory(new ReflectiveChannelFactory<Channel>(channelClass));
    }
}
