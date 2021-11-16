package com.amazonaws.lambda.demo;

import java.text.ParseException;
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
    final public String BIRTHDAY ="birthday";
    @Override
    public LinkedHashMap<String, String> handleRequest(LinkedHashMap<String, String> input, Context context) {
        Map<String,String> person = new LinkedHashMap<>(input);

        //DynamoDBに接続してHelloWorldDatabaseテーブルを更新する
        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(TABLENAME);
        //テーブルに格納するためにItemオブジェクトを呼び出す
        Item item = new Item();
        SimpleDateFormat now = new SimpleDateFormat("E,dd MMM yyyy HH:mm:ss +0000");
        SimpleDateFormat birthday = new SimpleDateFormat("yyyy/mm/dd");
        birthday.setLenient(false);
        item.with(ID, person.get(FIRSTNAME)+ " "+person.get(LASTNAME));
        item.with(LATESTGREETINGTIME,now.format(new Date()));
        String birthdayData = person.get(BIRTHDAY);
        //空文字チェック
        if(birthdayData == ""){
            //空文字の場合はnullを格納
            birthdayData = null;
        }else{
            try {
                //フォーマットに合った形かチェック
                birthday.parse(birthdayData);
                //入力されたデータがフォーマットに合っていた場合itemインスタンスに追加、今回はString型で入れる
                item.with(BIRTHDAY,birthdayData );
            } catch (ParseException e) {
                //フォーマットに従っていなかった場合はnullを格納
                item.with(BIRTHDAY, null);
            }
        }
        //テーブルの更新
        table.putItem(item);

        //htmlに返すjsonを用意する
        Map<String,String> result = new LinkedHashMap<>();
        result.put("body", "Hello from Lambda "+ person.get(FIRSTNAME)+ " "+person.get(LASTNAME));

        return (LinkedHashMap<String, String>) result;
    }

}
