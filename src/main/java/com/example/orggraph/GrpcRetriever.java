package com.example.orggraph;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.google.api.graphql.rejoiner.Schema;
import com.google.api.graphql.rejoiner.SchemaModule;
import com.google.api.graphql.rejoiner.SchemaProviderModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

public class GrpcRetriever<C> implements GraphQLRemoteRetriever<C> {
  private final AbstractModule clientModule;
  private final SchemaModule schemaModule;

  GrpcRetriever(AbstractModule clientModule, SchemaModule schemaModule) {
    this.clientModule = clientModule;
    this.schemaModule = schemaModule;
  }
  
  @Override
  public CompletableFuture<Map<String, Object>> queryGraphQL(ExecutionInput executionInput, C context) {
    GraphQLSchema schema = Guice.createInjector(new SchemaProviderModule(), clientModule, schemaModule)
        .getInstance(Key.get(GraphQLSchema.class, Schema.class));

    GraphQL graphql = GraphQL.newGraphQL(schema).build();

    ExecutionResult result = graphql.execute(executionInput);

    return CompletableFuture.completedFuture(result.toSpecification());
  }
}