//package org.phial.baas.manager.listener.fabric;
//
//import cn.hutool.core.date.DateTime;
//import cn.hutool.core.lang.Assert;
//import com.google.protobuf.InvalidProtocolBufferException;
//import org.apache.commons.codec.binary.Hex;
//import org.hyperledger.fabric.gateway.Network;
//import org.hyperledger.fabric.protos.common.Common;
//import org.hyperledger.fabric.sdk.BlockEvent;
//import org.hyperledger.fabric.sdk.BlockInfo;
//import org.hyperledger.fabric.sdk.BlockListener;
//import org.hyperledger.fabric.sdk.Channel;
//import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
//import org.phial.baas.fabric.client.ChannelClient;
//import org.phial.baas.manager.config.source.DynamicDataSourceContextHolder;
//import org.phial.baas.service.constant.CommonConstant;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Consumer;
//
//@Component
//public class FabricChannelBlockEventListener {
//    private static final Logger logger = LoggerFactory.getLogger(FabricChannelBlockEventListener.class);
//
//    // channelId : handle
//    private final Map<String, String> channelHandleListenerMap = new ConcurrentHashMap<>();
//
//    // channelId : listener
//    private final Map<String, Consumer<BlockEvent>> channelListeneConsumerMap = new ConcurrentHashMap<>();
//
//    //TODO
//    private final ExecutorService executor = new ThreadPoolExecutor(
//            10, 20, 5, TimeUnit.MINUTES,
//            new LinkedBlockingQueue<>(),
//            new ThreadPoolExecutor.CallerRunsPolicy());
//
//    @Resource
//    private FabricChannelGatewayHolder fabricChannelGatewayHolder;
//
//    @Resource
//    private FabricChainService fabricChainService;
//
//
//    public void addBlockListener(Channel channel) throws InvalidArgumentException {
//        if (channelHandleListenerMap.containsKey(channel.getName())) {
//            logger.warn("channel=" + channel.getName() + " BlockListener has exist!");
//            return;
//        }
//
//        String handle = channel.registerBlockListener(new BlockListener() {
//            @Override
//            public void received(BlockEvent blockEvent) {
//                try {
//                    String channelId = blockEvent.getChannelId();
//                    long blockNumber = blockEvent.getBlockNumber();
//                    String hash = Hex.encodeHexString(blockEvent.getDataHash());
//                    Common.Block blockData = blockEvent.getBlock();
//                    Iterable<BlockInfo.EnvelopeInfo> envelopeInfos = blockEvent.getEnvelopeInfos();
//                    Iterable<BlockEvent.TransactionEvent> transactionEvents = blockEvent.getTransactionEvents();
//
//                    logger.info("BlockListener received blockEvent channelId;{}, blockNumber:{}, hash:{}", channelId, blockNumber, hash);
//                } catch (Exception e) {
//                    logger.error("addListener received error:{}", e.getMessage(), e);
//                }
//            }
//        });
//
//        channelHandleListenerMap.putIfAbsent(channel.getName(), handle);
//    }
//
//    public void removeListener(Channel channel) {
//        try {
//            String handle = channelHandleListenerMap.get(channel.getName());
//            boolean ok = channel.unregisterBlockListener(handle);
//        } catch (Exception e) {
//            logger.error("removeListener error:{}", e.getMessage(), e);
//        }
//    }
//
//
//    /////////////////////////////////////////////
//    /////////////////////////////////////////////
//
//    public void addBlockListener(String channelId, long startBlockHeight) {
//        ChannelClient channelClient = fabricChannelGatewayHolder.getChannelClient(channelId);
//        Assert.notNull(channelClient, "channelId=[" + channelId + "] channelClient not exists!");
//
//        final FabricChain chain = fabricChainService.getByChainId(channelId);
//        Network network = channelClient.getNetwork();
//
//        final String source = DynamicDataSourceContextHolder.getCurrentSource();
//        final RedisClient redisClient = RedisClient.getInstance(source);
//
//        RedisKeyUtil redisKeyUtil = redisClient.getRedisKeyUtil();
//        String currentHeightKey = redisKeyUtil.getCurrentListenedHeight(channelId);
//        String currentTransactionSumKey = redisKeyUtil.getCurrentListenedCount(channelId);
//
//        long latestHeight = redisClient.getLong(currentHeightKey);
//        if (latestHeight == 0) {
//            latestHeight = chain.getHeightCount();
//        }
//        // 从当前最新区块高度开始监听
//        startBlockHeight = latestHeight;
//        long finalLatestHeight = latestHeight;
//        Consumer<BlockEvent> listener = network.addBlockListener(startBlockHeight, new Consumer<BlockEvent>() {
//            @Override
//            public void accept(BlockEvent blockEvent) {
//                executor.execute(() -> {
//
//                    DynamicDataSourceContextHolder.setCurrentSource(source);
//
//                    long blockNumber = blockEvent.getBlockNumber();
//                    String hash = Hex.encodeHexString(blockEvent.getDataHash());
////                    Common.Block blockData = blockEvent.getBlock();
////                    Iterable<BlockInfo.EnvelopeInfo> envelopeInfos = blockEvent.getEnvelopeInfos();
////                    Iterable<BlockEvent.TransactionEvent> transactionEvents = blockEvent.getTransactionEvents();
//                    if (finalLatestHeight == blockNumber) {
//                        return;
//                    }
//                    redisClient.setLong(currentHeightKey, blockNumber);
//                    long redisTransactionSum = redisClient.getLong(currentTransactionSumKey);
//                    long latestTransactionSum = redisTransactionSum + blockEvent.getTransactionCount();
//                    redisClient.setLong(currentTransactionSumKey, latestTransactionSum);
//                    logger.info("channelId:{} accept event current blockHeight:{}, hash:{}, transactionSum:{}",
//                            channelId, blockNumber, hash, latestTransactionSum);
//
//                    //分析这个block处在哪一天
//                    DateTime dateTime = null;
//                    try {
//                        dateTime = DateTime.of(blockEvent.getEnvelopeInfo(0).getTimestamp().getTime());
//                        String dayOfTheBlockSuffix = dateTime.toString(CommonConstant.DATE_PATTERN);
//                        String dayTransactionSumKey = redisKeyUtil.getDateListenedCount(channelId, dayOfTheBlockSuffix);
//
//                        //然后计算数量
//                        redisClient.incrBy(dayTransactionSumKey, blockEvent.getTransactionCount());
//                        long dayCount = redisClient.getLong(dayTransactionSumKey);
//                        logger.info("addBlockListener channelId:{}, txCount:{}, height:{}, day:{}, dayCount:{}",
//                                channelId, latestTransactionSum, blockNumber, dayTransactionSumKey, dayCount);
//
//                    } catch (InvalidProtocolBufferException e) {
//                        logger.error("calculateRunningInfo error:{}", e.getMessage(), e);
//                    }
//                });
//            }
//        });
//        channelListeneConsumerMap.putIfAbsent(channelId, listener);
//        logger.info("addBlockListener channelId:{}, startBlockHeight:{} success!", channelId, startBlockHeight);
//    }
//
//    public void removeBlockListener(String channelId) {
//        if (!channelListeneConsumerMap.containsKey(channelId)) {
//            return;
//        }
//        Consumer<BlockEvent> listener = channelListeneConsumerMap.get(channelId);
//        ChannelClient channelClient = fabricChannelGatewayHolder.getChannelClient(channelId);
//        Assert.notNull(channelClient, "channelId=[" + channelId + "] channelClient not exists!");
//
//        Network network = channelClient.getNetwork();
//        network.removeBlockListener(listener);
//    }
//
//}
