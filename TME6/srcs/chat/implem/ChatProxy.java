package srcs.chat.implem;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import srcs.chat.generated.*;
import srcs.grpc.util.BuilderUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ChatProxy implements Chat {

    private final MessageReceiver receiver;
    private final ManagedChannel channel;
    private final ChatGrpc.ChatStub asyncStub;
    private final ChatGrpc.ChatBlockingStub blockingStub;
    private Server callbackServer = null;

    public ChatProxy(String host, int port, MessageReceiver receiver) {
        this.receiver = receiver;
        channel = BuilderUtil.disableStat(ManagedChannelBuilder.forAddress(host, port).usePlaintext()).build();
        asyncStub = ChatGrpc.newStub(channel);
        blockingStub = ChatGrpc.newBlockingStub(channel);
    }

    @Override
    public boolean subscribe(String pseudo, String host, int port) {
        BoolValue subscribed = blockingStub.subscribe(SubscribeMessage.newBuilder().setPseudo(pseudo).setHost(host).setPort(port).build());

        if (subscribed.getValue()) {
            try {
                callbackServer = BuilderUtil.disableStat(ServerBuilder.forPort(port)
                        .addService(new MessageReceiverGrpc.MessageReceiverImplBase() {
                            @Override
                            public void newMessage(ChatMessage request, StreamObserver<Empty> responseObserver) {
                                receiver.newMessage(request.getPseudo(), request.getContent());
                                responseObserver.onNext(Empty.getDefaultInstance());
                                responseObserver.onCompleted();
                            }
                        })).build().start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return subscribed.getValue();
        /*CountDownLatch latch = new CountDownLatch(1);
        final boolean[] subscribed = {false};

        StreamObserver<BoolValue> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(BoolValue boolValue) {
                subscribed[0] = boolValue.getValue();
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                System.err.println("gRPC error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {}
        };

        // Sending request
        asyncStub.subscribe(SubscribeMessage.newBuilder().setPseudo(pseudo).setHost(host).setPort(port).build(), responseObserver);

        // Waiting for returned value
        try {
            latch.await();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }

        // Running the callback server on another thread
        if (subscribed[0]) {
            try {
                callbackServer = BuilderUtil.disableStat(ServerBuilder.forPort(port)
                .addService(new MessageReceiverGrpc.MessageReceiverImplBase() {
                    @Override
                    public void newMessage(ChatMessage request, StreamObserver<Empty> responseObserver) {
                        receiver.newMessage(request.getPseudo(), request.getContent());
                        responseObserver.onNext(Empty.getDefaultInstance());
                        responseObserver.onCompleted();
                    }
                })).build().start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return subscribed[0];*/
    }

    @Override
    public int send(String pseudo, String message) {
        Int32Value sent_to = blockingStub.send(ChatMessage.newBuilder().setPseudo(pseudo).setContent(message).build());
        return sent_to.getValue();
        /*CountDownLatch latch = new CountDownLatch(1);
        final int[] sent_to = {0};
        StreamObserver<Int32Value> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(Int32Value int32Value) {
                    sent_to[0] = int32Value.getValue();
                    latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                System.err.println("gRPC error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {}
        };

        // Sending request
        asyncStub.send(ChatMessage.newBuilder().setPseudo(pseudo).setContent(message).build(), responseObserver);

        // Waiting for returned value
        try {
            latch.await();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }

        return sent_to[0];*/
    }

    @Override
    public List<String> listChatter() {
        ListChatterMessage chatterList = blockingStub.listChatter(Empty.getDefaultInstance());
        return chatterList.getChatterList();
        /*CountDownLatch latch = new CountDownLatch(1);
        final List<String> chatterList = new LinkedList<>();

        StreamObserver<ListChatterMessage> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(ListChatterMessage listChatterMessage) {
                chatterList.addAll(listChatterMessage.getChatterList());
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                System.err.println("gRPC error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {}
        };

        asyncStub.listChatter(Empty.getDefaultInstance(), responseObserver);

        // Waiting for the returned value
        try {
            latch.await();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }

        return chatterList;*/
    }

    @Override
    public void unsubscribe(String pseudo) {
        blockingStub.unsubscribe(StringValue.of(pseudo));
        /*CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Empty> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(Empty empty) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                System.err.println("gRPC error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {}
        };

        asyncStub.unsubscribe(StringValue.of(pseudo), responseObserver);

        // Waiting for the returned value
        try {
            latch.await();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        } finally {
            if (callbackServer != null) {
                callbackServer.shutdown();
                try {
                    callbackServer.awaitTermination();
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                }
                callbackServer = null;
            }
        }*/
    }

    public void shutdown() {
        channel.shutdown();

        if (callbackServer != null) {
            callbackServer.shutdown();
            try {
                callbackServer.awaitTermination();
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
