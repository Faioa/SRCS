package srcs.chat.implem;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import srcs.chat.generated.*;
import srcs.grpc.util.BuilderUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class ChatImpl extends ChatGrpc.ChatImplBase {

    private final Map<String, ManagedChannel> directory;

    public ChatImpl() {
        directory = new ConcurrentHashMap<>();
    }

    @Override
    public void subscribe(SubscribeMessage request, StreamObserver<BoolValue> responseObserver) {
        boolean subscribed = false;
        if (!directory.containsKey(request.getPseudo())) {
            subscribed = true;
            directory.put(request.getPseudo(), BuilderUtil.disableStat(ManagedChannelBuilder.forAddress(request.getHost(), request.getPort()).usePlaintext()).build());
        }
        responseObserver.onNext(BoolValue.newBuilder().setValue(subscribed).build());
        responseObserver.onCompleted();
    }

    @Override
    public void send(ChatMessage request, StreamObserver<Int32Value> responseObserver) {
        int sent_to = 0;
        sent_to = directory.size();

        for (ManagedChannel channel : directory.values()) {
            MessageReceiverGrpc.MessageReceiverStub stub = MessageReceiverGrpc.newStub(channel);
            /*
             Sending the message in asynchronous mode. Can use empty handlers as we don't need to know if it succeeded.
             Using another Context is REQUIRED because the asynchronous calls try to cancel the calls on the channels after
             a delay if the result is not explicitly ended, and it leads to the Context being canceled and every channel
             using it are reset, causing error to be thrown with further utilisation.
             */
            Context ctx = Context.ROOT.withCancellation();
            ctx.run(() -> {
                stub.newMessage(request, new StreamObserver<Empty>() {
                    @Override
                    public void onNext(Empty empty) {}

                    @Override
                    public void onError(Throwable throwable) {}

                    @Override
                    public void onCompleted() {}
                });
            });
        }
        responseObserver.onNext(Int32Value.newBuilder().setValue(sent_to).build());
        responseObserver.onCompleted();
    }

    @Override
    public void listChatter(Empty request, StreamObserver<ListChatterMessage> responseObserver) {
        ListChatterMessage message = ListChatterMessage.newBuilder().addAllChatter(directory.keySet()).build();
        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void unsubscribe(StringValue request, StreamObserver<Empty> responseObserver) {
        ManagedChannel channel = directory.remove(request.getValue());
        if (channel != null)
            channel.shutdown();
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

}
