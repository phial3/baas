package org.phial.baas.manager.monitor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class SystemMonitorInitializer extends ChannelInitializer<SocketChannel> {

    private ByteBuf delimiter;
    private SystemMonitor monitor;

    public SystemMonitorInitializer(SystemMonitor monitor) {
        this(null, monitor);
    }

    public SystemMonitorInitializer(String delimiter, SystemMonitor monitor) {
        if (delimiter == null) delimiter = "\n";
        this.delimiter = Unpooled.copiedBuffer(delimiter.getBytes());
        this.monitor = monitor;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new IdleStateHandler(15, 30, 30, TimeUnit.SECONDS))
                .addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
                .addLast(new StringDecoder())
                .addLast(new StringEncoder())
                .addLast(new TerminalCommandHandler(monitor));
    }
}
