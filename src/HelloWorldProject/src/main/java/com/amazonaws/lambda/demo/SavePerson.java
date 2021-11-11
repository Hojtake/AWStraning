package com.amazonaws.lambda.demo;

import java.util.LinkedHashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SavePerson implements RequestHandler<LinkedHashMap<String, String>, LinkedHashMap<String, String>> {
    final public String FIRSTNAME = "firstName";
    final public String LASTNAME = "lastName";
    final public String TABLENAME = "HelloWorldDatabase";
    final public String ID = "ID";
    @Override
    public LinkedHashMap<String, String> handleRequest(LinkedHashMap<String, String> input, Context context) {
        context.getLogger().log("Input: " + input);
        // TODO: implement your handler

        Map<String,String> name = new LinkedHashMap<>(input);
        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable(TABLENAME);
        Item item = new Item();
        item.with(ID, name.get(FIRSTNAME)+ " "+name.get(LASTNAME));
        table.putItem(item);
        /*
         *
         * githubに上げて、リポジトリを新たに作る。
         * git for Windowsを入れてもよい
         * リポジトリ上げる時に神野さんに会議かけて*/
        Map<String,String> result = new LinkedHashMap<>();
        result.put("body", "Hello from Lambda "+ name.get(FIRSTNAME)+ " "+name.get(LASTNAME));
        return (LinkedHashMap<String, String>) result;
    }

}
