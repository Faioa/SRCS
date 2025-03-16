package srcs.chat.implem;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import srcs.chat.generated.*;
import srcs.grpc.util.BuilderUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            MessageReceiverGrpc.MessageReceiverFutureStub stub = MessageReceiverGrpc.newFutureStub(channel);
            // Sending the message in asynchronous mode. Discarding the Future as we don't want the result anyway
            stub.newMessage(request);
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
        directory.remove(request.getValue()).shutdown();
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

}
