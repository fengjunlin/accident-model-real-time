package com.fengjunlin.accident.utils;

import com.fengjunlin.accident.model.BaseDataPointWithSpeed;

import java.util.ArrayList;

/**
 * @Description
 * @Author fengjl
 * @Date 2019/8/25 18:42
 * @Version 1.0
 **/
public class test {
    public static void main(String[] args) {
        BaseDataPointWithSpeed baseDataPointWithSpeed = new BaseDataPointWithSpeed(2.0,3.0,56L,230.0,45L);
        BaseDataPointWithSpeed baseDataPointWithSpeed1= new BaseDataPointWithSpeed(2.0,3.0,56L,230.0,45L);
        ArrayList<Object> objects = new ArrayList<>();
        ArrayList<Object> objects1 = new ArrayList<>();

        objects.add(56);
        objects.add(54353);
        objects.add(33);
        objects.add(baseDataPointWithSpeed);
        objects.add("aa");
        objects1.add(33);
        objects1.add(888888);



        boolean b = objects1.addAll(objects);
        System.out.println(objects1);
        int i = objects1.indexOf(baseDataPointWithSpeed1);
        System.out.println(i);
    }
}
