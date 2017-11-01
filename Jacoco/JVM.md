- 关于JVM执行模型。Java虚拟机（JVM）是基于栈结构的，也就是Java代码运行在Java虚拟机线程中，而每一个线程拥有多个执行栈，执行栈由多个帧组成，每执行一个方法，则在执行栈中产生一个帧，即方法被触发时，一个新产生的帧将被Push到当前线程的执行栈中，当该方法执行完毕正常返回或者发生异常时，该帧将被Pop出执行栈。

- 关于帧。每个帧由本地变量（**Local Variables**）和操作栈（**Operand Stack**）两部分组成。
# 变量
## 局部变量

  ```java
  int i=5;
  ```

  被编译成：

  ```java
  0: bipush  5
  2: istore_0
  ```

| 操作码       | 说明                                       |
| :-------- | :--------------------------------------- |
| bipush  x | push一个Byte类型的x作为Int整型值到操作栈，上例中将5加入到操作栈   |
| istore_y  | 将操作栈中的相应的Int整型值变量pop出操作栈，然后存储到本地变量数组中,y表示在操作栈中的位置。 |

  内存中转换情况：

  ![](https://i.imgur.com/vB0fNeh.png)

## 成员变量

```java
public class SimpleClass {
    public int simpleField = 100;
}
```

被编译成;

```java
public SimpleClass();
  Code:
    stack=2, locals=1, args_size=1
       0: aload_0
       1: invokespecial #1      //调用Object的init构造方法
       4: aload_0
       5: bipush        100
       7: putfield      #2      //成员变量simpleField` 类型为int整型
      10: return
```

| --            | --                                       |
| ------------- | ---------------------------------------- |
| 操作码           | 说明                                       |
| aload_0       | 读取帧本地变量的值然后push到操作栈中。即从局部变量表的相应位置装载一个对象引用到操作数栈的栈顶。本例代码中没有构造函数，但是编译器会默认创建相应的构造函数，因此该变量实际上指向this，**aload_0把this装载到操作数栈中**。这里的0表示在局部变量表中的相应位置，然后将对象的引用装载到操作数栈中。 |
| invokespecial | 该指令调用实例的初始化方法，包含私有方法以及当前类的父类方法。它属于一组以不同方式调用方法的操作码，该组操作码有：invokedymanic、invokeinterface、invokestatic、invokevirtual。其中invokespecial用于调用父类构造方法的指令，例如java.lang.Object的构造方法。 |
| bipush        | 将int整型值放入操作栈中，这里是将100放入操作栈中。（该指令同上节）     |
| putfield      | 从运行时的常量池中取出一个指向成员变量的引用，然后该成员变量的值及其对应的对象都会从操作数栈中弹出。本例中，成员变量为SimpleField，首先第4步aload_0向操作数栈中添加了对象，然后bipush向 操作数栈中添加了100，最后putfield从栈中弹出这两个值，这个对象的simpleField的值被设为了100。 |

![](https://i.imgur.com/zYovoZ2.png)

# 条件选择

## if-else

```java
public int greaterThen(int intOne, int intTwo) {
    if (intOne > intTwo) {
        return 0;
    } else {
        return 1;
    }
}
```

该方法会编译成以下字节码：

```java
0: iload_1
1: iload_2
2: if_icmple     7
5: iconst_0
6: ireturn
7: iconst_1
8: ireturn
```

首先使用**iload_1**和**iload_2**将两个参数加载到操作栈中，然后使用if_Icomple比较操作栈顶的两个值大小，如果intOne小于intTwo，则跳转到位置7，否则继续。

**if_icomple**：比较栈顶两个int值大小，小于或等于0时跳转

**ireturn**：从当前方法返回int

**注：**

**iconst_0**表示将int类型的0 push进操作栈，该组还有其他类型的操作指令

| --          | --                                 |
| ----------- | ---------------------------------- |
| iconst_o    | push Int类型0进栈                      |
| aconst_null | push null进栈                        |
| fconst_0    | push float类型进栈                     |
| dconst_0    | push double类型进栈                    |
| bipush      | push byte类型进栈（单字节常量值-128~127）      |
| sipush      | push short类型进栈（端正新常量值-32768~32767） |



![](https://i.imgur.com/ZPVfioJ.png)

示例2：

```java
public int greaterThen(float floatOne, float floatTwo) {
    int result;
    if (floatOne > floatTwo) {
        result = 1;
    } else {
        result = 2;
    }
    return result;
}
```

编译成的字节码：

```java
0: fload_1
 1: fload_2
 2: fcmpl
 3: ifle          11
 6: iconst_1
 7: istore_3
 8: goto          13
11: iconst_2
12: istore_3
13: iload_3
14: ireturn
```

- **fload_1**和**fload_2**将参数推送至栈顶
- **fcmpl**：比较栈顶两个float类型值大小，将结果（1、0、-1）推送栈顶。若有一个数值为NaN，将-1推送栈顶
- **ifle**：栈顶int值小于0跳转
- **istore_3**：将栈顶int值传入第三个本地变量

![](https://i.imgur.com/CaAlIBX.png)

## Switch

switch表达式支持char、byte、short、int、string或enum类型，JVM对于Switch提供了两个指令：tableswitch以及lookupswitch，它们都支持对int值进行操作，而char、byte、short以及enum都可以在内部转换成为int。

- tableswitch：用于switch条件跳转，case值连续
- lookupswitch：用于switch条件跳转，case值不连续

### tableswitch

tableswitch是列出最大值与最小值之间所有的case值（不管它是否出现在Java代码中），如果JVM发现switch变量不在最值范围内，则直接跳转到default代码块，在最值范围内，如果出现在代码中，则跳转到相应位置，若没有出现在代码块中，跳转到default处。它可以保证最值范围内每个case值都有相应的结果。

示例：

```java
public int simpleSwitch(int intOne) {
    switch (intOne) {
        case 0:
            return 3;
        case 1:
            return 2;
        case 4:
            return 1;
        default:
            return -1;
    }
}
```

被编译成：

```java
0: iload_1
 1: tableswitch   {
         default: 42
             min: 0
             max: 4
               0: 36
               1: 38
               2: 42
               3: 42
               4: 40
    }
36: iconst_3
37: ireturn
38: iconst_2
39: ireturn
40: iconst_1
41: ireturn
42: iconst_m1
43: ireturn
```

这里tableswitch有0、1和4三个值，每个值对应了相应的代码块，同时还包含了最值范围内其他的值2和3，不过他们不是需要寻找的case值，所以指向了default处。

当指令执行时，首先判断是否在最值内，如果不在则直接转到default（default索引总是出现在第一个字节）处，如果她在最值中，则依次寻找满足的条件跳转到相应的位置。

![](https://i.imgur.com/Bg4695Y.png)

### lookupswitch

如果最值范围太大，而case值很稀疏，那么tableswitch则会消耗很多内存。此时使用lookupswitch更好，它只会列出对应的case值的跳转，而不是每一个介于最值间的跳转。

示例：

```java
public int simpleSwitch(int intOne) {
    switch (intOne) {
        case 10:
            return 1;
        case 20:
            return 2;
        case 30:
            return 3;
        default:
            return -1;
    }
}
```

被编译成：

```java
0: iload_1
 1: lookupswitch  {
         default: 42
           count: 3
              10: 36
              20: 38
              30: 40
    }
36: iconst_1
37: ireturn
38: iconst_2
39: ireturn
40: iconst_3
41: ireturn
42: iconst_m1
43: iretur
```

这里为了保证算法的性能，索引匹配的值是有序的。

![](https://i.imgur.com/XwvgxXH.png)

# 循环

## While循环

示例：

```java
public void whileLoop() {
    int i = 0;
    while (i < 2) {
        i++;
    }
}
```

```java
0: iconst_0
 1: istore_1
 2: iload_1
 3: iconst_2
 4: if_icmpge     13
 7: iinc          1, 1
10: goto          2
13: return
```

- **iconst_0**：将int型0放入操作栈
- **istore_1**：将操作栈顶0存入本地变量
- **iload_1**：将第二个本地变量推送至栈顶
- **iconst_2**：将int型2推送至栈顶
- **if_icmpge**：比较栈顶两个int型值大小，结果大于0时跳转（此时如果栈底大于栈顶值，则跳出while循环，转到13步，直接return）
- **iinc**：将指定int型变量增加指定值，这里**iinc 1，1**：在局部变量表第一个值加1

![](https://i.imgur.com/iDyyCIs.png)

![](https://i.imgur.com/Pe7yGJs.png)

## for循环

for循环与while循环在字节码层面上是相同的，因为所有while循环都可以转换成为for循环，例如上例中的while循环可以转换成如下的for循环，他们的字节码也是之前那个。

```java
public void forLoop() {
    for(int i = 0; i < 2; i++) {
        //do nothing
    }
}
```

## do-while循环

示例：

```java
public void doWhileLoop() {
    int i = 0;
    do {
        i++;
    } while (i < 2);
}
```

被编译成：

```jav
0: iconst_0
 1: istore_1
 2: iinc          1, 1
 5: iload_1
 6: iconst_2
 7: if_icmplt     2
10: return
```

- **iconst_0**：int型0放入操作栈
- **istore_1**：将0放入本地变量
- **iinc**：本地变量0加1
- **iload_1**：此处将本地变量载入操作栈（由于是i++，所以返回原来的值，还是0）
- **iconst_2**：将2载入栈顶
- **if_icmplt**：进行比较，如果大于0 ，转到第二步继续循环

![](https://i.imgur.com/FEe8svN.png)

![](https://i.imgur.com/iKzxWA7.png)

# 文献

[从Java代码到字节码](http://www.importnew.com/13107.html#more)

