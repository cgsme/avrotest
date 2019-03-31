package com.linewell.test;

import com.linewell.avro.User;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        User user1 = new User();
        user1.setName("Alyssa");
        user1.setFavoriteNumber(256);
        // favoriteColor = null

        // 通过构造函数
        User user2 = new User("Ben", 7, "red");

        // 通过build构造，会自动设置默认值
        User user3 = User.newBuilder()
                .setName("charlie")
                .setFavoriteColor("blue")
//                .setFavoriteNumber(null)
                .build();


        /*here:
        for (int i=0;i<100;i++) {
            System.out.println("==== i:"+i);
            there:
            for (int j=0;j<100;j++) {
                System.out.println("j=" + j);
                if (j == 10) {
                    break there;
                }
            }

        }
        System.out.println("over");*/
    }
}
