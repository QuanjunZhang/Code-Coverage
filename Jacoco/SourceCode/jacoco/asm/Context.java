package org.jacoco.asm;

class Context
{
  Attribute[] attrs;
  int flags;
  char[] buffer;
  int[] bootstrapMethods;
  int access;
  String name;
  String desc;
  Label[] labels;
  int typeRef;
  TypePath typePath;
  int offset;
  Label[] start;
  Label[] end;
  int[] index;
  int mode;
  int localCount;
  int localDiff;
  Object[] local;
  int stackCount;
  Object[] stack;
}

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.Context
 * JD-Core Version:    0.5.4
 */