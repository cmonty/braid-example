package com.example.orggraph;

import java.io.IOException;

import com.example.orggraph.PaymentGreeterGrpc.PaymentGreeterImplBase;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class PaymentServer {
    private Server server;

    private void start() throws IOException {
      int port = 50051;
      server = ServerBuilder.forPort(port).addService(new PaymentGreeterImpl()).build().start();
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          System.err.println("*** shutting down gRPC server since JVM is shutting down");
          PaymentServer.this.stop();
          System.err.println("*** server shut down");
        }
      });

    }

    private void stop() {
      if (server != null) {
        server.shutdown();
      }
    }

    private void blockUntilShutdown() throws InterruptedException {
      if (server != null) {
        server.awaitTermination();
      }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final PaymentServer server = new PaymentServer();
        server.start();
        server.blockUntilShutdown();

    }

    static class PaymentGreeterImpl extends PaymentGreeterImplBase {
      @Override
      public void getPayment(PaymentRequest request, StreamObserver<PaymentReply> responseObserver) {
        PaymentReply reply = PaymentReply.newBuilder().setAmountPaid("2.00").setAmountOwed("98.00").setStatus("overdue").build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
      }
    }
}