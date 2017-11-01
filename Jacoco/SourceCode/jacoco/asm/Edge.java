package org.jacoco.asm;

class Edge
{
  static final int NORMAL = 0;
  static final int EXCEPTION = 2147483647;
  int info;
  Label successor;
  Edge next;
}

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.Edge
 * JD-Core Version:    0.5.4
 */