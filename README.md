# braid-example

Example GraphQL server using [`graphql-braid`](https://bitbucket.org/atlassian/graphql-braid) to stitch separate schemas together from "microservices".

## To run

```bash
$ ./mvnw spring-boot:run
```

Tested on Java 11.

## Concepts

`graphql-braid` has a few undocumented concepts, I'll do my best to describe them below.

### SchemaProvider

The schema provider is responsible for returning a `Reader` representing the GraphQL SDL. In this example I wanted to provide those schemas from remote servers. As a result, I had to:

1. Make an introspection request to my remote server (both cases use [Apollo Launchpad](https://launchpad.graphql.com))
1. Parse the result as JSON and pass the `data` value to graphql-java's [`IntrospectionResultToSchema`](https://github.com/graphql-java/graphql-java/blob/9a2d4e97d582d35784f8687bb72402f92b785cd4/src/main/java/graphql/introspection/IntrospectionResultToSchema.java)
1. Print the resulting `Document` and then feed the `String` to `StringReader`

At the end of those steps I had a valid `Supplier<Reader>` that provided the GraphQL schema as represented by the server.

### Retreiver

Braid allows for two kinds of "retrievers": local or remote. Again, in my example I wanted to use a remote retriever. The implementation of the `GraphQLRemoteRetriever` interface is to actually make the HTTP call to the remote server by parsing the `query` and `variables` from the `ExecutionInput`.

Once the remote server responds, you can parse the JSON to an Object and return the completed future.

### Links

To expose types underneath stitched schemas, you can provide a list of "links". You basically tell Braid to expose a field in a top-level query (e.g. `user` nested in `order`) and Braid will make a bulk request to your `users` service.
