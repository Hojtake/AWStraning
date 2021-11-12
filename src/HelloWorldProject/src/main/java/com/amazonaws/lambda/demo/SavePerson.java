package com.amazonaws.lambda.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    final public String LATESTGREETINGTIME = "LatestGreetingTime";
    @Override
    public LinkedHashMap<String, String> handleRequest(LinkedHashMap<String, String> input, Context context) {
        Map<String,String> name = new LinkedHashMap<>(input);

        //DynamoDBに接続してHelloWorldDatabaseテーブルを更新する
        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(TABLENAME);
        //テーブルに格納するためにItemオブジェクトを呼び出す
        Item item = new Item();
        SimpleDateFormat now = new SimpleDateFormat("E,dd MMM yyyy HH:mm:ss +0000");
        item.with(ID, name.get(FIRSTNAME)+ " "+name.get(LASTNAME));
        item.with(LATESTGREETINGTIME,now.format(new Date()));
        //テーブルの更新
        table.putItem(item);

        //htmlに返すjsonを用意する
        Map<String,String> result = new LinkedHashMap<>();
        result.put("body", "Hello from Lambda "+ name.get(FIRSTNAME)+ " "+name.get(LASTNAME));

        return (LinkedHashMap<String, String>) result;
    }

}
