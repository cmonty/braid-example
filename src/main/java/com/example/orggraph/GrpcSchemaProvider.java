package com.example.orggraph;

import java.io.Reader;
import java.io.StringReader;

import com.google.api.graphql.rejoiner.Schema;
import com.google.api.graphql.rejoiner.SchemaModule;
import com.google.api.graphql.rejoiner.SchemaProviderModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;

public class GrpcSchemaProvider {
  private final AbstractModule clientModule;
  private final SchemaModule schemaModule;

  GrpcSchemaProvider(AbstractModule clientModule, SchemaModule schemaModule) {
    this.clientModule = clientModule;
    this.schemaModule = schemaModule;
  }

  public Reader get() {
    GraphQLSchema schema = Guice.createInjector(
          new SchemaProviderModule(),
          clientModule,
          schemaModule)
      .getInstance(Key.get(GraphQLSchema.class, Schema.class));
    
    String printedSchema = new SchemaPrinter().print(schema);
    return new StringReader(printedSchema);
  }
}