package com.example.calculator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.exceptions.MyException;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * @Author: GJJ
 * @Date: 2019/6/7
 * @Version 1.0
 **/
public class Calculator {


    /**
     * 操作符号集合
     */
    private static Set<String> set;

    /**
     * 所有存放的历史
     */
    private static Map<Integer, Stack<Double>> stackMap;

    /**
     * 异常信息存放类
     * key : message
     * key :data
     */
    private static Map<String, Object> map;

    /**
     * 计数器
     */
    private static Integer counter = 0;

    static {
        stackMap = new LinkedHashMap<>();
        map = new HashMap<>();
        set = new HashSet<>();
        set.add("+");
        set.add("-");
        set.add("*");
        set.add("/");
        set.add("sqrt");
        set.add("clear");
        set.add("undo");
    }

    /**
     * 存放数据记录
     *
     * @param string
     */
    public static void putItem(String string) {

        if (StringUtils.isBlank(string)) {
            map.put("message", "参数不能为空");
            map.put("data", string);
            throw new MyException(JSONObject.toJSONString(map));
        }
        //分隔字符串
        String[] split = Calculator.split(string);
        if (null == stackMap.get(counter) || stackMap.get(counter).empty()) {
            if (!split[0].matches("^-?\\d+(\\.\\d+)?$")) {
                map.put("message", "operator " + split[0] + "(position " + 1 + ")");
                map.put("data", split[0]);
                throw new MyException(JSONObject.toJSONString(map));
            }
        }
        for (int i = 0; i < split.length; i++) {
            switch (split[i]) {
                case "clear":
                    if (null != stackMap.get(counter)) {
                        stackMap.get(counter).clear();
                    }
                    continue;
                case "undo":
                    if (null == stackMap.get(counter - 1)) {
                        break;
                    }
                    stackMap.remove(counter);
                    counter--;
                    continue;
                default:
                    break;
            }
            counter++;
            Stack<Double> stack = new Stack<>();
            //取出上一次的值
            stack.addAll(Optional.ofNullable(stackMap.get(counter - 1)).orElse(new Stack<>()));
            if (split[i].matches("^-?\\d+(\\.\\d+)?$")) {
                stack.push(Double.valueOf(split[i]));
            }
            //运算符操作
            if (set.contains(split[i])) {
                Double operation;
                Double pop = stack.pop();
                if ("sqrt".equals(split[i])) {
                    operation = operation(pop, null, split[i]);
                    stack.push(operation);
                    stackMap.put(counter, stack);
                    continue;
                }
                //平方根操作
                if (stack.empty() && !"sqrt".equals(split[i])) {
                    List<String> list = new ArrayList<>();
                    for (int j = i + 1; j < split.length; j++) {
                        list.add(split[j]);
                    }
                    map.put("message", "operator" + split[i] + "(position: " + (i * 2 + 1) + "): insufficient parameters stack: " + pop);
                    map.put("data", list);
                    throw new MyException(JSONObject.toJSONString(map));
                }
                //其他运算符操作
                Double pop1 = stack.pop();
                operation = operation(pop, pop1, split[i]);
                //说明有不正常数据
                if (null == operation) {
                    map.put("message", "operator" + split[i] + "(position: " + i + "): insufficient parameters stack: " + pop1);
                    map.put("data", split[i]);
                    throw new MyException(JSONObject.toJSONString(map));
                }
                stack.push(operation);
            }
            //判断是否包含除了字符串和规定字符
            if (!split[i].matches("^-?\\d+(\\.\\d+)?$") && !set.contains(split[i])) {
                map.put("message", "请输入正确的字符");
                List<String> list = new ArrayList<>();
                for (int j = i + 1; j < split.length; j++) {
                    list.add(split[j]);
                }
                map.put("data", list);
                stackMap.put(counter, stack);
                throw new MyException(JSONObject.toJSONString(map));
            }
            stackMap.put(counter, stack);
        }
    }

    /**
     * 计算操作
     *
     * @param first
     * @param secend
     * @param operat
     * @return
     */
    public static Double operation(Double first, Double secend, String operat) {
        switch (operat) {
            case "-":
                return secend - first;
            case "+":
                return secend + first;
            case "*":
                return secend * first;
            case "/":
                return secend / first;
            case "sqrt":
                return Math.sqrt(first);
            default:
        }
        return null;
    }


    /**
     * 获取用户输入的值，返回每一个元素
     *
     * @param item 输入的值
     * @return
     */
    public static String[] split(String item) {
        if (StringUtils.isBlank(item)) {
            map.put("message", "参数不能为空");
            map.put("data", item);
            throw new MyException(JSONObject.toJSONString(map));
        }
        String[] split = item.split(" ");
        return split;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入数值和操作符以空格分隔(结束请按N;重开请按R):");
            String item = scanner.nextLine();
            if ("N".equals(item.toUpperCase())) {
                return;
            }
            if ("R".equals(item.toUpperCase())) {
                stackMap.clear();
                continue;
            }
            try {
                Calculator.putItem(item);
            } catch (Exception e) {
                JSONObject jsonObject = JSONObject.parseObject(e.getMessage());
                System.out.println(jsonObject.get("message"));
                Object data = jsonObject.get("data");
                if (data instanceof JSONArray) {
                    JSONArray array = (JSONArray) data;
                    System.out.print("(the ");
                    for (int i = 0; i < array.size(); i++) {
                        if (i == array.size() - 1) {
                            System.out.print(array.get(i));
                            break;
                        }
                        System.out.print(array.get(i) + " and ");
                    }
                    System.out.print(" were not pushed on to the stack due to the previous error)\n");
                }
                if (data instanceof JSONObject) {
                    JSONObject object = (JSONObject) data;
                    System.out.println(object);
                }
                continue;
            }
            if (null == stackMap.get(counter)) {
                System.out.print("stack:" + "\n");
                continue;
            }
            System.out.print("stack:");
            stackMap.get(counter).forEach((x) -> {
                String regex = "-?(\\d+)\\.?[0]$";
                Pattern p = Pattern.compile(regex);
                if (p.matcher(String.valueOf(x)).matches()) {
                    DecimalFormat format = new DecimalFormat("#");
                    System.out.print(format.format(x) + " ");
                } else {
                    if (x.toString().length() - x.toString().indexOf(".") > 10) {
                        System.out.print(x.toString().substring(0, x.toString().indexOf(".") + 11) + " ");
                    } else {
                        System.out.print(x + " ");
                    }
                }
            });
            System.out.println();
        }
    }
}
