package org.maullu.resolver;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.maullu.service.HelloService;

public class QueryResolver implements DataFetcher<String> {

    private final HelloService helloService;

    public QueryResolver(HelloService helloService) {
        this.helloService = helloService;
    }

    @Override
    public String get(DataFetchingEnvironment dataFetchingEnvironment) {
        return helloService.getHello();
    }
}