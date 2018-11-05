package com.example.orggraph;

import com.google.api.graphql.rejoiner.Query;
import com.google.api.graphql.rejoiner.SchemaModule;

final class PaymentSchemaModule extends SchemaModule {
  @Query("payment")
  PaymentReply getPayment(PaymentRequest request, PaymentGreeterGrpc.PaymentGreeterBlockingStub client) {
    return client.getPayment(request);
  }
}