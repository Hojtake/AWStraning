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
    final public String TABLENAME = "HelloWorldDatabase";
    final public String COL_ID = "ID";
    final public String COL_LATESTGREETINGTIME = "LatestGreetingTime";
    final public String COL_BIRTHDAY ="birthday";
    @Override
    public LinkedHashMap<String, String> handleRequest(LinkedHashMap<String, String> input, Context context) {
        Map<String,String> person = new LinkedHashMap<>(input);

        //DynamoDBに接続してHelloWorldDatabaseテーブルを更新する
        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(TABLENAME);
        String age = "--";
        //データベースに格納するためにItemオブジェクトを呼び出し、登録する
        Item item = new Item();
        SimpleDateFormat now = new SimpleDateFormat("E,dd MMM yyyy HH:mm:ss +0000");
        SimpleDateFormat birthday = new SimpleDateFormat("yyyy/mm/dd");
        birthday.setLenient(false);
        item.with(COL_ID, person.get("firstName")+ " "+person.get("lastName"));
        item.with(COL_LATESTGREETINGTIME,now.format(new Date()));
        String birthdayData = person.get(COL_BIRTHDAY);

        if(birthdayData == ""){
            //仕様に従い空白の場合もnullに変換する
            birthdayData = null;
        }else{
            try {
                birthday.parse(birthdayData);
                //簡易に計算できるように設定した日付表現
                SimpleDateFormat calAge = new SimpleDateFormat("yyyy.MMdd");
                //整数部分を年、小数部分を月日にした数値で計算し、整数型にキャストすることで年齢を出す。
                age = String.valueOf(
                        Double.parseDouble(calAge.format(new Date()))
                        -Double.parseDouble(calAge.format(birthday.parse(birthdayData))));
                //今回仕様に指定がないのでString型でデータベースに登録する
                item.with(COL_BIRTHDAY,birthdayData );
            } catch (ParseException e) {
                //フォーマットに従っていなかった場合は仕様に従いnullを格納する
                item.with(COL_BIRTHDAY, null);
            }
        }
        table.putItem(item);

        person.put("age",age);

        return (LinkedHashMap<String, String>) person;
    }

}
