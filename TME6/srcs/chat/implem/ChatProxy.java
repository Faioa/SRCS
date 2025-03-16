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
import java.util.List;

public class ChatProxy implements Chat {

    private final MessageReceiver receiver;
    //private final ManagedChannel asyncChannel;
    private final ManagedChannel blockingChannel;
    //private final ChatGrpc.ChatStub asyncStub;
    private final ChatGrpc.ChatBlockingStub blockingStub;
    private Server callbackServer = null;

    public ChatProxy(String host, int port, MessageReceiver receiver) {
        this.receiver = receiver;
        //asyncChannel = BuilderUtil.disableStat(ManagedChannelBuilder.forAddress(host, port).usePlaintext()).build();
        blockingChannel = BuilderUtil.disableStat(ManagedChannelBuilder.forAddress(host, port).usePlaintext()).build();
        //asyncStub = ChatGrpc.newStub(asyncChannel);
        blockingStub = ChatGrpc.newBlockingStub(blockingChannel);
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
    }

    @Override
    public int send(String pseudo, String message) {
        Int32Value sent_to = blockingStub.send(ChatMessage.newBuilder().setPseudo(pseudo).setContent(message).build());
        return sent_to.getValue();
    }

    @Override
    public List<String> listChatter() {
        ListChatterMessage chatterList = blockingStub.listChatter(Empty.getDefaultInstance());
        return chatterList.getChatterList();
    }

    @Override
    public void unsubscribe(String pseudo) {
        blockingStub.unsubscribe(StringValue.of(pseudo));
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
