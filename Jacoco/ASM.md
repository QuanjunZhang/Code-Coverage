# 字节码操纵框架ASM
Jacoco注入探针来进行覆盖率分析，主要使用的是ASM库。ASM是Java字节码操纵框架，它能够读取class文件，改变类行为，分析类信息，甚至能够生成自定义的新类。
# ASM中核心类
- ClassReader：该类用来解析字节码class文件,具体可以直接由字节组或者class文件间接的获得字节码数据。可以调用accept方法，这个方法接受一个实现了ClassVisitor接口的对象作为参数，然后依次调用ClassVisitor接口的各个方法。
- ClassWriter：该类实现了ClassVisitor接口，用来重新构建编译后的类（是框架的核心部分），比如说修改类名、属性以及方法，生成新的字节码文件。具体而言，它可以以二进制的方式创建一个类的字节码，对于ClassWriter的每一个方法的调用会创建类的相应部分，例如调用visit方法会创建一个类的声明部分，调用visitMethod方法就会在这个类中创建一个新的方法，调用visitEnd方法表明对于该类的创建已经完成了。它最终会通过toByteArray方法返回一个数组，这个数组包含了整个class文件的完整字节码内容。
- ClassAdapter：该类也实现了ClassVisitor接口，其构造刚发需要ClassVisitor对象，并保存字段为protected ClassVisitor cv。在它的实现中，每个方法都是原装不动的调用cv的对应方法，并传递同样的参数。**可以通过继承ClassAdapter并修改其中的部分方法达到过滤的作用，它可以看成事件的过滤器**

## ClassVisitor

抽象类ClassVisitor成员函数

```java
public abstract class ClassVisitor {
    public ClassVisitor(int api);
    public ClassVisitor(int api, ClassVisitor cv);
  //访问类头部信息
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces);
    public void visitSource(String source, String debug);
    public void visitOuterClass(String owner, String name, String desc); 
    AnnotationVisitor visitAnnotation(String desc, boolean visible); 
    public void visitAttribute(Attribute attr);
    public void visitInnerClass(String name, String outerName, String innerName, int access);
  //访问变量
    public FieldVisitor visitField(int access, String name, String desc,
    String signature, Object value);
  //访问方法
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions); 
  //结束
    void visitEnd();
}
```

## MethodVisitor

```java
abstract class MethodVisitor { // public accessors ommited 
  MethodVisitor(int api); 
  MethodVisitor(int api, MethodVisitor mv);
  AnnotationVisitor visitAnnotationDefault();
  AnnotationVisitor visitAnnotation(String desc, boolean visible);
  AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible); 
  void visitAttribute(Attribute attr); 
  //Starts the visit of the method's code, if any (i.e. non abstract method).
  //访问函数开始时调用
  void visitCode(); 
  void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack);
  // Visits a zero operand instruction.
  void visitInsn(int opcode);
  void visitIntInsn(int opcode, int operand); 
  //Visits a local variable instruction. A local variable instruction is an
  // instruction that loads or stores the value of a local variable.
  void visitVarInsn(int opcode, int var);
  void visitTypeInsn(int opcode, String desc);
  void visitFieldInsn(int opc, String owner, String name, String desc);
  //Visits a method instruction. A method instruction is an instruction that
  // invokes a method.
  void visitMethodInsn(int opc, String owner, String name, String desc); 
  void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs); 
  void visitJumpInsn(int opcode, Label label);
  void visitLabel(Label label);
  void visitLdcInsn(Object cst); 
  void visitIincInsn(int var, int increment); 
  void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels);
  void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels); 
  void visitMultiANewArrayInsn(String desc, int dims); 
  void visitTryCatchBlock(Label start, Label end, Label handler, String type);
  void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index);
  void visitLineNumber(int line, Label start);
  //操作栈和本地变量的最大值
  void visitMaxs(int maxStack, int maxLocals); 
  void visitEnd();
}
```

### visitInsn()
** visitInsn(int opcode)**
  参数可以是如下指令

  ```java
   *            This opcode is
   *            either NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1,
   *            ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1,
   *            FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD,
   *            LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
   *            IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE,
   *            SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1,
   *            DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
   *            IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
   *            FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
   *            IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D,
   *            L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
   *            LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
   *            DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER,
   *            or MONITOREXIT.
  ```

### visitVarInsn()
**visitVarInsn(int opcode, int var);**

  第一个参数可以是如下指令：

  ```java
  This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
  ```

  第二个参数是局部变量表中的地址
### visitMethodInsn()
**visitMethodInsn(int opc, String owner, String name, String desc); **

  示例：`mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");`

  参数:

  - **opcode**:`INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC ,INVOKEINTERFACE.`
  - **owner**:该类的internal name
  - name：方法名
  - desc：描述符
#　Java与字节码类型转换

![](https://i.imgur.com/PzoOIFO.png)

方法类型转换示例：

![](https://i.imgur.com/n57YFfH.png)

#具体用例

## 生成自定义接口

```java
package pkg;
public interface Comparable extends Mesurable {
    int LESS = -1;
    int EQUAL = 0;
    int GREATER = 1;
    int compareTo(Object o);
```

ASM代码：

```java
ClassWriter cw = new ClassWriter(0);
//定义类的头部信息
cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "pkg/Comparable", null, "java/lang/Object", new String[] { "pkg/Mesurable" });
//定义变量
cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd();
cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I", null, new Integer(0)).visitEnd();
cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I", null, new Integer(1)).visitEnd(); 
//添加函数，但是这样创建出来的函数没有具体的实现字节码，只是一个函数名
cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I", null, null).visitEnd(); 
cw.visitEnd();
byte[] b = cw.toByteArray();
```
### Visit()示例

- **visit(int version, int access, String name, String signature, String superName, String[] interfaces);**定义类的头部信息

  | param                   | 参数      | 示例                                       | 解释           |
  | ----------------------- | ------- | ---------------------------------------- | ------------ |
  | **int version**         | 版本号     | **V1_5**                                 | 1.5版本        |
  | **int access**          | 访问标志    | **ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE** | 公有抽象接口       |
  | **String name**         | 类的全限定名称 | **"pkg/Comparable"**                     | Comparable类名 |
  | **String signature**    | 泛型签名    | **null**                                 |              |
  | **String superName**    | 父类      | **"java/lang/Object"**                   | 父类是Object    |
  | **String[] interfaces** | 接口      | **new String[] { "pkg/Mesurable" }**     | 实现该接口        |

源码示例：
```java
cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "pkg/Comparable", null, "java/lang/Object", new String[] { "pkg/Mesurable" });
```
生成：
  ```java
  public interface Comparable extends Mesurable
  ```


- **visitField(int access, String name, String desc,String signature, Object value);**


### visitField()示例

源码示例：

```jav
cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd();
```

生成：

```java
int LESS = -1;
```

**desc**表示方法的描述符，这里使用"**I**"表示**int**类型

### VisitMethod示例（构造函数）

Api：**visitMethod(int access, String name, String desc, String signature, String[] exceptions); **

- 示例：

```java
public class MyClass{
    public MyClass(){
        //do somethig
    }
}
```

其方法是：

```java
classAdapter.visitMethod(Opcodes.ACC_PUBLIC,"<init>","()V", null,null).visitCode();
//定义构造方法 
```

- 实现细节：

关于具体的字节码可以在Javap里查看

```java
Code:  
       0: aload_0  
       1: invokespecial #10                 // Method java/lang/Object."<init>":()V  
       4: return 
```

因此我们在MethodVisitor里面定义该构造函数的实现字节码

```java
mv.visitCode();  
    mv.visitVarInsn(ALOAD, 0);  
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");  
    mv.visitInsn(RETURN);  
    mv.visitMaxs(1, 1);  
    mv.visitEnd();  
```

构造方法的本地方法与操作数栈大小分别为1，是因为aload_0指令表示从局部变量表加载一个reference类型值到操作数栈，这里局部变量表只有this引用；

### visitMethod示例（成员函数）

对于以下的函数

```java
public int add(int a, int b);  
```

我们为其添加实现细节，使之如下：

```java
public int add(int a, int b) {  
        return a + b;  
    } 
```

对于该函数，我们使用Javap查看其具体的字节码如下：

```java
public int add(int, int);  
    Code:  
       0: iload_1  
       1: iload_2  
       2: iadd  
       3: ireturn  
```

那么我们可以在访问该函数时这样操作来满足要求

```java
// 添加add方法  
    mv.visitCode();  
    mv.visitVarInsn(ILOAD, 1);  
    mv.visitVarInsn(ILOAD, 2);  
    mv.visitInsn(IADD);  
    mv.visitInsn(IRETURN);  
    mv.visitMaxs(2, 3);  
    mv.visitEnd();  
```

add方法的操作数栈大小为2，局部变量表大小为3，分别为this,a,b。

## 删除方法

在ClassVisitor的visitMethod方法中，它对于每一个方法，返回了一个MethodVisitor对象

```java
public MethodVisitor visitMethod(int ac                              cess, String name, String desc,
            String signature, String[] exceptions) {
        if (cv != null) {
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
        return null;
    }
```

那么我们如果想要删除某个方法，需要重写该方法，直接返回null即可。

```java
public class RemoveMethodAdapter extends ClassVisitor {
    private String mName;
    private String mDesc;
    public RemoveMethodAdapter(ClassVisitor cv, String mName, String mDesc) {
        super(ASM4, cv);
        this.mName = mName;
        this.mDesc = mDesc;
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(mName) && desc.equals(mDesc)) {
          //满足条件，之间返回null，相当于删除
            // do not delegate to next visitor -> this removes the method
            return null;
        }
      //其他函数正常返回
        return cv.visitMethod(access, name, desc, signature, exceptions);
    }
}
```

## 增加变量

以增加变量为例，在ClassVisitor的visitEnd函数中，增加相应的变量，增加变量使用的是visitField函数 。

```java 
public class AddFieldAdapter extends ClassVisitor {
    private int fAcc;
    private String fName;
    private String fDesc;
    private boolean isFieldPresent;
    public AddFieldAdapter(ClassVisitor cv, int fAcc, String fName, String fDesc) {
        super(ASM4, cv);
        this.fAcc = fAcc;
        this.fName = fName;
        this.fDesc = fDesc;
    }
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (name.equals(fName)) {
          //判断是否存在该变量
            isFieldPresent = true;
        }
        return cv.visitField(access, name, desc, signature, value);
    }
    @Override
    public void visitEnd() {
        if (!isFieldPresent) {//该变量不存在
          //直接传入需要增加的变量，使用visitField函数返回FieldVisitor对象进行增加
            FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        cv.visitEnd();
    }
}
```



# NOTE

- visitInsn、visitVarInsn、visitMethodInsn等以Insn结尾的方法可以添加方法实现的字节码



