项目主页：[http://www.eclemma.org/jacoco/](http://www.eclemma.org/jacoco/)

# Content

- [调用的开源框架](#调用的开源框架)
  - [Ant](#ant)
  - [ASM](#asm)
- [测试源码对比](#测试源码对比)
  - [插入前源码](#插入前源码)
  - [插入后源码](#插入后源码)
  - [插入前字节码](#插入前字节码)
  - [插入后字节码](#插入后字节码)
- [两种插桩模式](#两种插桩模式)
  - [插桩方式](#插桩方式)
  - [Offline](#offline)
  - [On-the-fly](#on-the-fly)
  - [比较](#比较)
- [关于switch插桩分析](#关于switch插桩分析)
  - [TableSwitch](#tableswitch)
  - [Lookupswitch](#lookupswitch)
- [Report生成](#report生成)
  - [Task report](#task-report)
    - [Element executiondata](#element-executiondata)
    - [Element structure](#element-structure)
    - [Element xml](#element-xml)
    - [Element check](#element-check)
- [插桩策略](#插桩策略)
- [源码分析](#源码解析)
  - [插桩](#插桩)
  - [生成报告](#生成报告)
    - [获得覆盖率信息](#获得覆盖率信息)
    - [报告的生成](报告的生成)
- [参考](#参考)



# 调用的开源框架

## Ant
开发文档：[http://www.jacoco.org/jacoco/trunk/doc/ant.html](http://www.jacoco.org/jacoco/trunk/doc/ant.html)

## ASM
项目主页：[http://asm.ow2.org/](http://asm.ow2.org/)

ASM分析：[ASM Analyse](./ASM)

# 测试源码对比
## 插入前源码

```java
public class Hello
{
  public Hello()
  {
    int rand = (int)(Math.random() * 100.0D);
    if (rand % 2 == 0)
      System.out.println("Hi,0");
    else {
      System.out.println("Hi,1");
    }
    System.out.println("End");
  }
}
```

```java
public class HelloTest
{
  public static void main(String[] args)
  {
    Hello h = new Hello();
  }
}
```

## 插入后源码


```java
public class Hello
{
  public Hello()
  {
    arrayOfBoolean[0] = true;
    int rand = (int)(Math.random() * 100.0D);
    if (rand % 2 == 0) { arrayOfBoolean[1] = true;
      System.out.println("Hi,0"); arrayOfBoolean[2] = true;
    } else {
      System.out.println("Hi,1"); arrayOfBoolean[3] = true;
    }
    System.out.println("End");
    arrayOfBoolean[4] = true;
  }
}
```

```java
public class HelloTest
{
  public HelloTest()
  {
    arrayOfBoolean[0] = true;
  }
  public static void main(String[] arg0) {
    boolean[] arrayOfBoolean = $jacocoInit(); Hello h = new Hello();
    arrayOfBoolean[1] = true;
  }
}
```
## 插入前字节码
```bash
E:\git\Code-Coverage\Jacoco\example\target\classes>javap -c HelloTest
Compiled from "HelloTest.java"
public class HelloTest {
  public HelloTest();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: new           #2                  // class Hello
       3: dup //dup为了invokespecial存在，保持对示例的引用
       4: invokespecial #3                  // Method Hello."<init>":()V
       7: astore_1
       8: return
}
```

```bash
F:\Jacoco\target\classes>javap -c Hello
Compiled from "Hello.java"
public class Hello {
  public Hello();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: invokestatic  #2                  // Method java/lang/Math.random:()D
       7: ldc2_w        #3                  // double 100.0d
      10: dmul
      11: d2i
      12: istore_1
      13: iload_1
      14: iconst_2
      15: irem
      16: ifne          30
      19: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
      22: ldc           #6                  // String Hi,0
      24: invokevirtual #7                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      27: goto          38
      30: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
      33: ldc           #8                  // String Hi,1
      35: invokevirtual #7                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      38: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
      41: ldc           #9                  // String End
      43: invokevirtual #7                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      46: return
}
```

## 插入后字节码

```bash
Compiled from "HelloTest.java"
public class HelloTest {
  public HelloTest();
    Code:
       0: invokestatic  #28                 // Method $jacocoInit:()[Z
       3: astore_1
       4: aload_0
       5: invokespecial #1                  // Method java/lang/Object."<init>":()V
       8: aload_1
       9: iconst_0
      10: iconst_1
      11: bastore
      12: return

  public static void main(java.lang.String[]);
    Code:
       0: invokestatic  #28                 // Method $jacocoInit:()[Z
       3: astore_1
       4: new           #2                  // class Hello
       7: dup
       8: invokespecial #3                  // Method Hello."<init>":()V
      11: astore_2
      12: aload_1
      13: iconst_1
      14: iconst_1
      15: bastore
      16: return
}
```



```bash
F:\Jacoco\target\classes-instr>javap -c Hello
Compiled from "Hello.java"
public class Hello {
  public Hello();
    Code:
       0: invokestatic  #49                 // Method $jacocoInit:()[Z
       3: astore_1
       4: aload_0
       5: invokespecial #1                  // Method java/lang/Object."<init>":()V
       8: aload_1
       9: iconst_0
      10: iconst_1
      11: bastore
      12: invokestatic  #2                  // Method java/lang/Math.random:()D
      15: ldc2_w        #3                  // double 100.0d
      18: dmul
      19: d2i
      20: istore_2
      21: iload_2
      22: iconst_2
      23: irem
      24: ifne          46
      27: aload_1
      28: iconst_1
      29: iconst_1
      30: bastore
      31: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
      34: ldc           #6                  // String Hi,0
      36: invokevirtual #7                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      39: aload_1
      40: iconst_2
      41: iconst_1
      42: bastore
      43: goto          58
      46: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
      49: ldc           #8                  // String Hi,1
      51: invokevirtual #7                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      54: aload_1
      55: iconst_3
      56: iconst_1
      57: bastore
      58: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
      61: ldc           #9                  // String End
      63: invokevirtual #7                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      66: aload_1
      67: iconst_4
      68: iconst_1
      69: bastore
      70: return
}
```



# 两种插桩模式

## 插桩方式
![](http://i.imgur.com/EukDdHy.png)

## Offline
>先对字节码文件进行插桩，然后执行插桩后的字节码文件，生成覆盖信息并导出报告。

## On-the-fly
>JVM通过-javaagent参数指定特定的jar文件启动Instrumentation代理程序，代理程序在装载class文件前判断是否已经转换修改了该文件，若没有则将探针（统计代码）插入class文件，最后在JVM执行测试代码的过程中完成对覆盖率的分析。

## 比较
- On-the-fly更加方便获取代码覆盖率
  - 无需提前进行字节码插桩
  - 无需停机（Offline需要停机），可以实时获取覆盖率
- Offline无需额外开启代理
- Offline使用场景（[From Jacoco Documentation](http://www.jacoco.org/jacoco/trunk/doc/offline.html)）
  - 运行环境不支持Java Agent
  - 部署环境不允许设置JVM参数
  - 字节码需要被转换成其他虚拟机字节码，如Android Dalvik Vm
  - 动态修改字节码文件和其他Agent冲突
  - 无法自定义用户加载类




# 关于switch插桩分析

JVM分析：[JVM Analyse](JVM)

## TableSwitch

源码：

```java
 public void testSwitch(int i){
      switch(i) {
        case 1:
        System.out.println("1");
        break;
        case 2:
        System.out.println("2");    
        break;
        case 3:
        System.out.println("3"); 
        break;
        case 4:
        System.out.println("4");
        break;
        case 10:
        System.out.println("10");
        break;
        default:
        System.out.println("...");
        break;
        }//switch

    }
```

插入后反编译Java源码：

```java
public void testSwitch(int arg1) { 
    boolean[] arrayOfBoolean = $jacocoInit(); 
    switch (i)
    {
    case 1:
      System.out.println("1");
      arrayOfBoolean[4] = true; break;
    case 2:
      System.out.println("2");
      arrayOfBoolean[5] = true; break;
    case 3:
      System.out.println("3");
      arrayOfBoolean[6] = true; break;
    case 4:
      System.out.println("4");
      arrayOfBoolean[7] = true; break;
    case 10:
      System.out.println("10");
      arrayOfBoolean[8] = true; break;
    case 5:
    case 6:
    case 7:
    case 8:
    case 9:
    default:
      System.out.println("..."); 
      arrayOfBoolean[9] = true;
    }
     arrayOfBoolean[10] = true; 
 } 
```

我们可以发现，每一处label处都插入了探针，以及最后的return处也插入了一个探针。

源码字节码文件：

```java
 public void testSwitch(int);
    Code:
       0: iload_1
       1: tableswitch   { // 1 to 10
                     1: 56
                     2: 67
                     3: 78
                     4: 89
                     5: 111
                     6: 111
                     7: 111
                     8: 111
                     9: 111
                    10: 100
               default: 111
          }
      56: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      59: ldc           #8                  // String 1
      61: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      64: goto          119
      67: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      70: ldc           #10                 // String 2
      72: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      75: goto          119
      78: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      81: ldc           #11                 // String 3
      83: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      86: goto          119
      89: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      92: ldc           #12                 // String 4
      94: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      97: goto          119
     100: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
     103: ldc           #13                 // String 10
     105: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     108: goto          119
     111: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
     114: ldc           #14                 // String ...
     116: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     119: return
```

插入后class文件字节码：

```java
 public void testSwitch(int);
    Code:
       0: invokestatic  #65                 // Method $jacocoInit:()[Z
       3: astore_2
       4: iload_1
       5: tableswitch   { // 1 to 10
                     1: 60
                     2: 75
                     3: 90
                     4: 106
                     5: 138
                     6: 138
                     7: 138
                     8: 138
                     9: 138
                    10: 122
               default: 138
          }
      60: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      63: ldc           #8                  // String 1
      65: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      //case 1探针
      68: aload_2
      69: iconst_4
      70: iconst_1
      71: bastore
      72: goto          151
      75: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      78: ldc           #10                 // String 2
      80: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        //case 2探针
      83: aload_2
      84: iconst_5
      85: iconst_1
      86: bastore
      87: goto          151
      90: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      93: ldc           #11                 // String 3
      95: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        //case 3 探针
      98: aload_2
      99: bipush        6
     101: iconst_1
     102: bastore
     103: goto          151
     106: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
     109: ldc           #12                 // String 4
     111: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       //case 4 探针
     114: aload_2
     115: bipush        7
     117: iconst_1
     118: bastore
     119: goto          151
     122: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
     125: ldc           #13                 // String 10
     127: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       //case 10探针
     130: aload_2
     131: bipush        8
     133: iconst_1
     134: bastore
     135: goto          151
     138: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
     141: ldc           #14                 // String ...
     143: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       //default 探针
     146: aload_2
     147: bipush        9
     149: iconst_1
     150: bastore
     //return 探针
     151: aload_2
     152: bipush        10
     154: iconst_1
     155: bastore
     156: return
```

可以发现，在每一个label处都执行了探针插入（具体在goto指令前），return前也插入了一个探针。

## LookupSwitch

lookupswitch与tableswitch类似，依旧是每一个label和return处插入探针。

其插桩后的class文件反编译：

```java
 public void testSwitch(int arg1) { boolean[] arrayOfBoolean = $jacocoInit(); switch (i)
    {
    case 1:
      System.out.println("1");
      arrayOfBoolean[4] = true; break;
    case 2:
      System.out.println("2");
      arrayOfBoolean[5] = true; break;
    case 3:
      System.out.println("3");
      arrayOfBoolean[6] = true; break;
    case 4:
      System.out.println("4");
      arrayOfBoolean[7] = true; break;
    case 1000:
      System.out.println("10");
      arrayOfBoolean[8] = true; break;
    default:
      System.out.println("..."); 
      arrayOfBoolean[9] = true;
    }
    arrayOfBoolean[10] = true; 
  } 
```

# Report生成

在结束插桩后，再次运行class文件，会产生一个*Jacoco.exec*的文件，里面存放了探针的执行信息，显示如下：

![](https://i.imgur.com/HsOnZaq.png)

![](https://i.imgur.com/eTNqZKE.png)

下面会用这个信息来生成代码覆盖率。

## Task report

[Ant Task Report](http://www.jacoco.org/jacoco/trunk/doc/ant.html#report)

我们使用report task来生成不同格式的报告。report task声明包含以下几个部分，<executiondata>和<structure>指定输入的数据；<html/>指定输出的格式。

```xml
<!-- Step 4: Create coverage report -->
		<jacoco:report>

			<!-- This task needs the collected execution data and ... -->
			<executiondata>
              <!-- Jacoco.exec-->
				<file file="${result.exec.file}" />
			</executiondata>

			<!-- the class files and optional source files ... -->
			<structure name="JaCoCo Ant Example">
				<classfiles>
					<!-- ./target/classes：未插桩前的class文件字节码-->
					<fileset dir="${result.classes.dir}" />
				</classfiles>
				<sourcefiles encoding="UTF-8">
					<!-- /src：Java源码处-->
					<fileset dir="${src.dir}" />
				</sourcefiles>
			</structure>

			<!-- to produce reports in different formats. -->
			<html destdir="${result.report.dir}" />
		</jacoco:report>
```

如上所看到的的，report task是基于几个嵌套的元素的。

### Element executiondata

这个元素指定的Ant resource和resource collections，它们包含了Jacoco 的execution data files（如上图所示）。如果指定了超过了一种execution data，那么execution data将被合并。在输入文件的任何地方，代码块被标记成这样，那么就被认为是执行了。

### Element structure

这个元素定义了报告结构，它包含以下嵌套元素。

- **classfiles**：容器元素，它指定了Java class files，archive （档案文件）files（jar，war，ear etc，或者Pack200）或者包含class文件的文件夹。在档案或者文件夹中的class文件被递归查询。
- **sourcefiles**：可选的容器元素，指定了相关的源文件。如果源代码被指定，报告将会包含高亮代码。源文件可以被指定为独立文件或者文件目录。

Sourcefiles元素可以有以下可选的属性。

| 属性       | 描述               | 默认     |
| -------- | ---------------- | ------ |
| encoding | 源码的字符编码          | 平台默认编码 |
| tabwidth | 一个tab字符占的空白字符的数量 | 4字符    |

structure可以被精炼为group子元素，这种方式的覆盖报告可以反映项目的不同的模块。对每一个group元素相关的class文件和源代码可以被分别指定。例如：

```xml
<structure name="Example Project">
    <group name="Server">
        <classfiles>
            <fileset dir="${workspace.dir}/org.jacoco.example.server/classes"/>
        </classfiles>
        <sourcefiles>
            <fileset dir="${workspace.dir}/org.jacoco.example.server/src"/>
        </sourcefiles>
    </group>
    <group name="Client">
        <classfiles>
            <fileset dir="${workspace.dir}/org.jacoco.example.client/classes"/>
        </classfiles>
        <sourcefiles>
            <fileset dir="${workspace.dir}/org.jacoco.example.client/src"/>
        </sourcefiles>
    </group>    
    ...
</structure>
```

### Element xml

使用xml格式创建一个单独的报告。

| 属性       | 描述      | 默认    |
| -------- | ------- | ----- |
| destfile | 报告的放置位置 | 无     |
| encoding | 报告的编码   | UTF-8 |

### Element check

这个元素并没有在`build.xml`中出现，但是在`ReportTask.java`中有一个`CheckFormatterElement`,它并没有创建独立的报告（像html，xml和cvs格式的报告），该元素根据配置的rule检查coverage counters以及报告的不合法处。每一个rule应用于给定的类型（class，package，bundle，etc）的元素，用于检查每一个元素的rule有一系列的限制。下面的例子用于检查每一个包中行覆盖率最少在80%并且没有class被遗漏。

```xml
<check>
    <rule element="PACKAGE">
        <limit counter="LINE" value="COVEREDRATIO" minimum="80%"/>
        <limit counter="CLASS" value="MISSEDCOUNT" maximum="0"/>
    </rule>
</check>
```

check元素有以下的属性

| 属性                 | 描述                                     | 默认   |
| ------------------ | -------------------------------------- | ---- |
| rules              | 用于检查的`rules`集合                         | 无    |
| failonviolation    | 判定 rule violations情况下创建是否失败            | true |
| violationsproperty | 存在violation messages的ant property元素的名称 | 无    |

我们可以发现，在<check>元素中，有<rule>元素，<rule>元素中有<limit>，事实上，<rule>和<limit>元素都可以被嵌套。

- 在check元素中，任何数量的rule元素可以被嵌套

| 属性       | 描述                                       | 默认     |
| -------- | ---------------------------------------- | ------ |
| element  | rule应用的element，可以取值:`bundle`,`package`,`class`,`sourcefile`和`method` | bundle |
| includes | 应当被检查的元素集合名                              | *      |
| excludes | 不需要被检查的元素                                | empty  |
| limits   | 用于检查的`limits`                            | none   |

- 在rule元素中，任何数量的limit元素可以被嵌套

| 属性      | 描述                                       | 默认           |
| :------ | :--------------------------------------- | ------------ |
| counter | 被检查的counter，可以是： `INSTRUCTION`, `LINE`, `BRANCH`, `COMPLEXITY`, `METHOD` and `CLASS`. | INSTRUCTION  |
| value   | 需要被检查的counter的值，可以是： `TOTALCOUNT`, `MISSEDCOUNT`, `COVEREDCOUNT`, `MISSEDRATIO` and `COVEREDRATIO`. | COVEREDRATIO |
| minimum | 期望的最小值。                                  | none         |
| maximum | 期望的最大值。                                  | none         |



# 插桩策略

对于Java源码：

```java
public static void example() {
    a();
    if (cond()) {
        b();
    } else {
        c();
    }
    d();
}
```

编译后转换为字节码：

```java
public static example()V
      INVOKESTATIC a()V
      INVOKESTATIC cond()Z
      IFEQ L1
      INVOKESTATIC b()V
      GOTO L2
  L1: INVOKESTATIC c()V
  L2: INVOKESTATIC d()V
      RETURN
```

这样我们可以使用ASM框架在字节码文件中进行插桩操作，具体的是插入探针probe，一般是Boolean数组，下面是原始的控制流图，以及插桩完成的控制流图。

![这里写图片描述](http://img.blog.csdn.net/20171023123117318?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvb2hjZXp6eg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

由Java字节码定义的控制流图有不同的类型，每个类型连接一个源指令和一个目标指令，当然有时候源指令和目标指令并不存在，或者无法被明确（异常）。不同类型的插入策略也是不一样的。

![这里写图片描述](http://img.blog.csdn.net/20171023123950294?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvb2hjZXp6eg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

下面说明如何在不同的边缘去情况下具体的插入探针。

![这里写图片描述](http://img.blog.csdn.net/20171023133835866?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvb2hjZXp6eg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

Probe探针是通过以下四个指令来设置的。

```java
ALOAD    probearray
xPUSH    probeid
ICONST_1
BASTORE
```

注意到探针是线程安全的，它不会改变操作栈和本地数组。它也不会通过外部的调用而离开函数。先决条件仅仅是探针数组作为一个本地变量被获取。在每个函数的开始，附加的指令代码将会插入以获得相应类的数组对象，避免代码复制，这个初始化会在静态私有方法`$jacocoinit()`中进行。

具体详见：[Control Flow Analysis for Java Methods](http://www.jacoco.org/jacoco/trunk/doc/flow.html)

 # 源码解析

整个工具主要分为两个部分，对编译好的字节码文件插桩以及根据探针的执行情况生成报告。在`build.xml`中，二者的代码分别是。

插桩:

```xml
<target name="instrument" depends="compile">
		<!-- Step 2: Instrument class files -->
		<!-- jacoco:instrument见antlib.xml定义-->
		<jacoco:instrument destdir="${result.classes.instr.dir}">
			<fileset dir="${result.classes.dir}" />
		</jacoco:instrument>
	</target>
```

```xml
 <taskdef name="instrument" classname="org.jacoco.ant.InstrumentTask"/>
```

生成报告：

```xml
	<target name="report" depends="test">
		<!-- Step 4: Create coverage report -->
		<jacoco:report>

			<!-- This task needs the collected execution data and ... -->
			<executiondata>
				<file file="${result.exec.file}" />
			</executiondata>

			<!-- the class files and optional source files ... -->
			<structure name="JaCoCo Ant Example">
				<classfiles>
					<!-- ./target/classes：未插桩前的class文件字节码-->
					<fileset dir="${result.classes.dir}" />
				</classfiles>
				<sourcefiles encoding="UTF-8">
					<!-- /src：Java源码处-->
					<fileset dir="${src.dir}" />
				</sourcefiles>
			</structure>

			<!-- to produce reports in different formats. -->
			<html destdir="${result.report.dir}" />
			<csv destfile="${result.report.dir}/report.csv" />
			<xml destfile="${result.report.dir}/report.xml" />
		</jacoco:report>
	</target>

```

```xml
 <taskdef name="report" classname="org.jacoco.ant.ReportTask"/>
```

## 插桩

在插桩中，程序入口是`org.jacoco.ant.InstrumentTask`，向其传入了两个参数`destdir`和`fileset`，分别是存放插入后的字节码文件位置以及字节码文件。

在`InstrumentTask`类中，由于是自定义Ant Task，所以执行函数是`excute()`，在`instrument()`函数中调用`Instrumenter`类，在`instrument(final ClassReader reader)`函数中，有以下代码：

```java
     final ClassWriter writer = new ClassWriter(reader, 0);

     final IProbeArrayStrategy strategy = ProbeArrayStrategyFactory
                .createFor(reader, accessorGenerator);

     final ClassVisitor visitor = new ClassProbesAdapter(
                new ClassInstrumenter(strategy, writer), true);

        reader.accept(visitor, ClassReader.EXPAND_FRAMES);

```

可以看出来，`ClassProbesAdapter`应该是ASM框架中的适配器（即继承自ClassVisitor，自定义对字节码文件过滤的类），同时在`ClassInstrumenter`中，发现其`visitMethod()`函数返回了`MethodInstrumenter`对象，在该类中，找到了具体的插桩方法。

首先在`MethodProbesAdapter`中，定义了插桩策略。示例:

```java
	public void visitInsn(final int opcode) {
		switch (opcode) {
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
		case Opcodes.ARETURN:
		case Opcodes.RETURN:
		case Opcodes.ATHROW:
			probesVisitor.visitInsnWithProbe(opcode, idGenerator.nextId());
			break;
		default:
			probesVisitor.visitInsn(opcode);
			break;
		}
	}
```

然后在`MethodInstrumenter`中具体实现了各个策略。示例：

```java
	public void visitJumpInsnWithProbe(final int opcode, final Label label,
			final int probeId, final IFrame frame) {
		if (opcode == Opcodes.GOTO) {
			probeInserter.insertProbe(probeId);
			mv.visitJumpInsn(Opcodes.GOTO, label);
		} else {
			final Label intermediate = new Label();
			mv.visitJumpInsn(getInverted(opcode), intermediate);
			probeInserter.insertProbe(probeId);
			mv.visitJumpInsn(Opcodes.GOTO, label);
			mv.visitLabel(intermediate);
			frame.accept(mv);
		}
	}
```

具体插入是`probeInserter.insertProbe(probeId);`，它在`ProbeInster`中被实现：

```java
	public void insertProbe(final int id) {

		// For a probe we set the corresponding position in the boolean[] array
		// to true.

		mv.visitVarInsn(Opcodes.ALOAD, variable);

		// Stack[0]: [Z

		InstrSupport.push(mv, id);

		// Stack[1]: I
		// Stack[0]: [Z

		mv.visitInsn(Opcodes.ICONST_1);

		// Stack[2]: I
		// Stack[1]: I
		// Stack[0]: [Z

		mv.visitInsn(Opcodes.BASTORE);
	}
```

到这里插桩实现。

## 生成报告

生成报告在程序入口在`ReportTask`中，传入了executionData，sourcefiles和classfiles。其中executionData是再次运行被插桩的字节码问价获得的探针执行情况，在`report生成`一节有介绍。

关于其获得覆盖下信息后具体的生成报告的步骤，可以通过在jacoco中插入桩点进行运行分析，具体是在ReportTask中进行，其主要是获得bundle（拥有整个项目的覆盖率），之后通过bundle来生成覆盖率信息。

### 获得覆盖率信息

详见[Jacoco覆盖率信息收集1](http://blog.csdn.net/ohcezzz/article/details/78562275)

[Jacoco覆盖率信息收集2](http://blog.csdn.net/ohcezzz/article/details/78561430)

[Jacoco覆盖率信息收集3](http://blog.csdn.net/ohcezzz/article/details/78564430)

[Jacoco覆盖率信息收集4](http://blog.csdn.net/ohcezzz/article/details/78569347)

其对源码分析的最小单位为函数，对于一个函数的分析详见

[MethodAnalyzer分析](http://blog.csdn.net/ohcezzz/article/details/78555128)

### 报告的生成

下面就是在HTMLFormatter中将bundle信息传入sessionPage，BundlePage以及下面的packagePage，ClassPage，PackageSourcePage等。如果想要直接获得其信息而不用html展示则可以如下。

[Jacoco获得各个Counter信息](http://blog.csdn.net/ohcezzz/article/details/78569286)



 # 参考

[ Java字节码操纵框架ASM小试](http://blog.csdn.net/mr__fang/article/details/54729555)

[使用 ASM 实现 Java 语言的“多重继承” IBM](https://www.ibm.com/developerworks/cn/java/j-lo-asm/)

[从Java代码到字节码ImportNew翻译](http://www.importnew.com/13107.html#more)

[简书ASM创建函数示例](http://www.jianshu.com/p/760229bfe18a)

[伯乐在线，简述ASM各种文档](http://hao.jobbole.com/asm/)

[官方文档的中文简单版](http://www.apmbe.com/java-asm%E5%BA%93%E7%9A%84%E5%8E%9F%E7%90%86%E4%B8%8E%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95%EF%BC%88%E4%B8%80%EF%BC%89/)

[中文简单版2](http://www.apmbe.com/java-asm%E5%BA%93%E7%9A%84%E5%8E%9F%E7%90%86%E4%B8%8E%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95%EF%BC%88%E4%BA%8C%EF%BC%89/)

[ASM-Guide](http://download.forge.objectweb.org/asm/asm4-guide.pdf)

[AOP 的利器：ASM 3.0 介绍 IBM](https://www.ibm.com/developerworks/cn/java/j-lo-asm30/)

[ASM系列1-5 有广告那个](http://victorzhzh.iteye.com/category/140253)

[ASM系列](http://yunshen0909.iteye.com/category/341298)

[拥有构造函数以及成员函数两种示例](http://blog.csdn.net/aesop_wubo/article/details/48948211)

[添加时间，输出那个](http://www.cnblogs.com/liuling/archive/2013/05/25/asm.html)

[美团博客，关于Jacoco很深讲的](https://tech.meituan.com/android-jacoco-practace.html)

- 4/12/2017:

考虑mock与jacoco使用

[[Java单元测试(Junit+Mock+代码覆盖率)](http://www.cnblogs.com/AloneSword/p/4109407.html)](http://www.cnblogs.com/AloneSword/p/4109407.html)





