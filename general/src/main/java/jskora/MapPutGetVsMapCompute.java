package jskora;

import java.util.HashMap;
import java.util.Map;

public class MapPutGetVsMapCompute {

    public static void main(String[] args) {

        Map<String, Integer> map1 = new HashMap<>();
        map1.put("dog", 1);
        map1.put("cat", 0);

        // lookup, add, put
        map1.put("dog", (map1.get("dog")==null ? 0 : map1.get("dog")) + 1);
        /*
       L3
        LINENUMBER 15 L3
        ALOAD 1
        LDC "dog"
        ALOAD 1
        LDC "dog"
        INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object;
        IFNONNULL L4
        ICONST_0
        GOTO L5
       L4
       FRAME FULL [[Ljava/lang/String; java/util/Map] [java/util/Map java/lang/String]
        ALOAD 1
        LDC "dog"
        INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object;
        CHECKCAST java/lang/Integer
        INVOKEVIRTUAL java/lang/Integer.intValue ()I
       L5
       FRAME FULL [[Ljava/lang/String; java/util/Map] [java/util/Map java/lang/String I]
        ICONST_1
        IADD
        INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
        INVOKEINTERFACE java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        POP
         */

        // compute
        map1.compute("cat", (k, v) -> v == null ? 1 : v + 1);
        /*
       L6
        LINENUMBER 44 L6
        ALOAD 1
        LDC "cat"
        INVOKEDYNAMIC apply()Ljava/util/function/BiFunction; [
          // handle kind 0x6 : INVOKESTATIC
          java/lang/invoke/LambdaMetafactory.metafactory(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
          // arguments:
          (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;,
          // handle kind 0x6 : INVOKESTATIC
          jskora/MapPutGetVsMapCompute.lambda$main$0(Ljava/lang/String;Ljava/lang/Integer;)Ljava/lang/Integer;,
          (Ljava/lang/String;Ljava/lang/Integer;)Ljava/lang/Integer;
        ]
        INVOKEINTERFACE java/util/Map.compute (Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;
        POP
       L7
        LINENUMBER 46 L7
        RETURN
         */
    }

}
