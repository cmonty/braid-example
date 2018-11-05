package com.example.orggraph;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import graphql.ExecutionInput;
import graphql.ExecutionResult;

import java.io.Reader;
import java.util.ArrayList;
import java.util.function.Supplier;

import com.atlassian.braid.Braid;
import com.atlassian.braid.BraidGraphQL;
import com.atlassian.braid.Link;
import com.atlassian.braid.SchemaNamespace;
import com.atlassian.braid.source.QueryExecutorSchemaSource;
import com.google.gson.Gson;

import org.springframework.stereotype.Controller;

@Controller
public class GraphQLController {
    private final SchemaNamespace USERS = SchemaNamespace.of("users");
    private final String USERS_SCHEMA_URL = "https://7r7zjrl1rj.lp.gql.zone/graphql";

    private final SchemaNamespace ORDERS = SchemaNamespace.of("orders");
    private final String ORDERS_SCHEMA_URL = "https://rrm1vmjxxn.lp.gql.zone/graphql";

    private final SchemaNamespace PAYMENTS = SchemaNamespace.of("payments");

    @PostMapping(path="/graphql", consumes="application/json", produces="application/json")
    public @ResponseBody String graphql(@RequestBody GraphQLParameters params) {
        Gson gson = new Gson();
        Supplier<Reader> usersSchemaProvider = () -> new RemoteIntrospection(USERS_SCHEMA_URL).get();
        Supplier<Reader> ordersSchemaProvider = () -> new RemoteIntrospection(ORDERS_SCHEMA_URL).get();
        Supplier<Reader> paymentsSchemaProvider = () -> new GrpcSchemaProvider(new PaymentClientModule(), new PaymentSchemaModule()).get();

        ArrayList<Link> links = new ArrayList();
        links.add(Link.from(ORDERS, "Order", "user").to(USERS, "User").build());

        Braid braid = Braid
            .builder()
            .schemaSource(
                QueryExecutorSchemaSource
                    .builder()
                    .namespace(PAYMENTS)
                    .schemaProvider(paymentsSchemaProvider)
                    .remoteRetriever(new GrpcRetriever(new PaymentClientModule(), new PaymentSchemaModule()))
                    .build())
            .schemaSource(
                QueryExecutorSchemaSource
                    .builder()
                    .namespace(USERS)
                    .schemaProvider(usersSchemaProvider)
                    .remoteRetriever(new RemoteRetriever(USERS_SCHEMA_URL))
                    .build())
            .schemaSource(
                QueryExecutorSchemaSource
                    .builder()
                    .namespace(ORDERS)
                    .schemaProvider(ordersSchemaProvider)
                    .remoteRetriever(new RemoteRetriever(ORDERS_SCHEMA_URL))
                    .links(links)
                    .build())
            .build();

        BraidGraphQL graphql = braid.newGraphQL();

        ExecutionResult result = graphql
            .execute(
                ExecutionInput
                    .newExecutionInput()
                    .query(params.getQuery())
                    .build())
            .join();

        return gson.toJson(result.toSpecification());
    }
}