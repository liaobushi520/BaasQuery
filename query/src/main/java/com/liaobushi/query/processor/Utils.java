package com.liaobushi.query.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liaozhongjun on 2017/10/20.
 */

class Utils {

    static String removeParentheses(String s) {
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    static boolean isSuperInterface(Class checked, Class superInterface) {
        Class[] checkedInterfaces = checked.getInterfaces();
        if (checkedInterfaces == null || checkedInterfaces.length <= 0) {
            return false;
        }
        List<Class> interfaces = Arrays.asList(checkedInterfaces);
        List<Class> tempArray = new ArrayList<>();
        while (interfaces.size() > 0) {
            for (Class c : interfaces) {
                if (superInterface.getCanonicalName().equals(c.getCanonicalName())) {
                    return true;
                }
            }
            tempArray.clear();
            for (Class c : interfaces) {
                Class[] arr = c.getInterfaces();
                if (arr != null && arr.length > 0) {
                    tempArray.addAll(Arrays.asList(arr));
                }
            }
            interfaces = tempArray;
        }
        return false;
    }
}
