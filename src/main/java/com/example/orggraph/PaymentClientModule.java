package com.example.orggraph;

import com.google.inject.AbstractModule;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

final class PaymentClientModule extends AbstractModule {
  private static final String HOST = "localhost";
  private static final int PORT = 50051;

  @Override
  protected void configure() {
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext(true).build();
    bind(PaymentGreeterGrpc.PaymentGreeterBlockingStub.class).toInstance(PaymentGreeterGrpc.newBlockingStub(managedChannel));
  }
}