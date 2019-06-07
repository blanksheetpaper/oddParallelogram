package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 根据一个奇数，求平行四边形
 */
public class OddParallelogram {

    /**
     * 获取小于输入数字的所有的奇数
     *
     * @param str
     * @return
     */
    public static List<Integer> getOddNumber(String str) {
        List<Integer> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("[0-9]*");
        if (pattern.matcher(str).matches()) {
            int i = Integer.valueOf(str);
            if (i % 2 == 0) {
                return null;
            }
            for (int j = 0; j <= i; j++) {
                if (j % 2 != 0) {
                    list.add(j);
                }
            }
            return list;
        }

        return null;
    }

    /**
     * 获取半边结果集
     *
     * @param list
     * @return
     */
    public static List<List<Object>> getResults(List<Integer> list) {
        List<List<Object>> results = new ArrayList<>();
        if (null != list && !list.isEmpty()) {
            if (list.size() != 1) {
                List<Object> temp;
                for (int i = 0; i < list.size(); i++) {
                    temp = new ArrayList<>();
                    if (i == 0) {
                        temp.add(list.get(0));
                        results.add(temp);
                        continue;
                    }

                    for (int j = 0; j < i + 1; j++) {
                        if (j == 0) {
                            temp.add(list.get(i));
                            continue;
                        }
                        String s = Integer.valueOf(results.get(i - 1).get(j - 1).toString()) + Integer.valueOf(temp.get(j - 1).toString()) + "";
                        temp.add(s);
                    }
                    results.add(temp);
                }
            } else {
                List<Object> objects = new ArrayList<>();
                objects.add(list.get(0));
                results.add(objects);
            }
        }
        return results;
    }


    /**
     * 返回一半边的带空格的数据
     *
     * @param list
     */
    public static List<List<Object>> dealWithResult(List<List<Object>> list) {
        if (null != list && !list.isEmpty()) {
            List<Object> temp;  // 当前集合
            List<Object> before;  //前一个集合
            List<List<Object>> results = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                List<Object> objectList = new ArrayList<>();
                temp = list.get(i);
                //第一行不需要做处理
                if (i == 0) {
                    for (int j = 0; j < temp.size(); j++) {
                        if (j == temp.size() - 1) {
                            objectList.add(temp.get(j) + " ");
                            break;
                        }
                        objectList.add(temp.get(j) + " ");
                    }
                    results.add(objectList);
                    continue;
                }
                //其他行需要做处理
                //先解决前缀空格;
                //求开头需要加的空格数
                int row = i - 0;
                int spaceSum = 0;
                for (int j = 0; j < row; j++) {
                    spaceSum += list.get(0).get(j).toString().trim().length() + 1;
                }
                StringBuilder str = new StringBuilder();
                for (int j = 0; j < spaceSum; j++) {
                    str.append(" ");
                }
                objectList.add(str);
                //中间需要加的空格数
                before = list.get(0);
                for (int j = 0; j < temp.size(); j++) {
                    //每个间隔多少个空格
                    int length = getSpaceNum(before.get(i + j).toString().trim(), temp.get(j).toString().trim());
                    StringBuilder sb = new StringBuilder(temp.get(j).toString().trim());
                    for (int k = 0; k <= length; k++) {
                        sb.append(" ");
                    }
                    objectList.add(sb.toString());
                }
                results.add(objectList);
            }
            return results;
        }
        return null;
    }

    /**
     * 计算两个树之间差多少空格弥补
     *
     * @param before 上一行的此位数
     * @param now    当前行的数
     * @return
     */
    public static int getSpaceNum(String before, String now) {
        int number = before.length() - now.length();
        return number;
    }

    /**
     * 测试方法
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("请输入数值：");
        Scanner scan = new Scanner(System.in);
        String text = scan.next();
        List<Integer> list = getOddNumber(text);
        if (null != list && !list.isEmpty()) {
            List<List<Object>> l = getResults(list);
            Collections.reverse(l);
            l = l.stream().map((x) -> {
                Collections.reverse(x);
                return x;
            }).collect(Collectors.toList());
            l = dealWithResult(l);
            //重新放置copy到一个集合中，避免下面的 tow反转操作修改了数据信息
            List<List<Object>> listList = new ArrayList<>();
            for (int i = 0; i < l.size(); i++) {
                List<Object> objectList = new ArrayList<>();
                for (int j = 0; j < l.get(i).size(); j++) {
                    objectList.add(l.get(i).get(j));
                }
                listList.add(objectList);
            }
            List<List<Object>> tow = new ArrayList<>();
            tow.addAll(l);
            Collections.reverse(tow);
            tow = tow.stream().map((x) -> {
                Collections.reverse(x);
                return x;
            }).collect(Collectors.toList());
            List<List<Object>> lists = new ArrayList<>();
            for (int i = 0; i < listList.size(); i++) {
                List<Object> list1 = new ArrayList<>();
                list1.addAll(listList.get(i));
                list1.addAll(tow.get(i));
                lists.add(list1);
            }
            lists.stream().forEach((x) -> {
                x.stream().forEach(System.out::print);
                System.out.println();
            });

        }

    }

}
