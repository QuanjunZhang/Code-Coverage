/*     */ package org.jacoco.asm;
/*     */ 
/*     */ public class TypePath
/*     */ {
/*     */   public static final int ARRAY_ELEMENT = 0;
/*     */   public static final int INNER_TYPE = 1;
/*     */   public static final int WILDCARD_BOUND = 2;
/*     */   public static final int TYPE_ARGUMENT = 3;
/*     */   byte[] b;
/*     */   int offset;
/*     */ 
/*     */   TypePath(byte[] b, int offset)
/*     */   {
/*  85 */     this.b = b;
/*  86 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  95 */     return this.b[this.offset];
/*     */   }
/*     */ 
/*     */   public int getStep(int index)
/*     */   {
/* 108 */     return this.b[(this.offset + 2 * index + 1)];
/*     */   }
/*     */ 
/*     */   public int getStepArgument(int index)
/*     */   {
/* 122 */     return this.b[(this.offset + 2 * index + 2)];
/*     */   }
/*     */ 
/*     */   public static TypePath fromString(String typePath)
/*     */   {
/* 135 */     if ((typePath == null) || (typePath.length() == 0)) {
/* 136 */       return null;
/*     */     }
/* 138 */     int n = typePath.length();
/* 139 */     ByteVector out = new ByteVector(n);
/* 140 */     out.putByte(0);
/* 141 */     for (int i = 0; i < n; ) {
/* 142 */       char c = typePath.charAt(i++);
/* 143 */       if (c == '[') {
/* 144 */         out.put11(0, 0);
/* 145 */       } else if (c == '.') {
/* 146 */         out.put11(1, 0);
/* 147 */       } else if (c == '*') {
/* 148 */         out.put11(2, 0);
/* 149 */       } else if ((c >= '0') && (c <= '9')) {
/* 150 */         int typeArg = c - '0';
/* 151 */         while ((i < n) && ((c = typePath.charAt(i)) >= '0') && (c <= '9')) {
/* 152 */           typeArg = typeArg * 10 + c - 48;
/* 153 */           ++i;
/*     */         }
/* 155 */         if ((i < n) && (typePath.charAt(i) == ';')) {
/* 156 */           ++i;
/*     */         }
/* 158 */         out.put11(3, typeArg);
/*     */       }
/*     */     }
/* 161 */     out.data[0] = (byte)(out.length / 2);
/* 162 */     return new TypePath(out.data, 0);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 174 */     int length = getLength();
/* 175 */     StringBuilder result = new StringBuilder(length * 2);
/* 176 */     for (int i = 0; i < length; ++i) {
/* 177 */       switch (getStep(i))
/*     */       {
/*     */       case 0:
/* 179 */         result.append('[');
/* 180 */         break;
/*     */       case 1:
/* 182 */         result.append('.');
/* 183 */         break;
/*     */       case 2:
/* 185 */         result.append('*');
/* 186 */         break;
/*     */       case 3:
/* 188 */         result.append(getStepArgument(i)).append(';');
/* 189 */         break;
/*     */       default:
/* 191 */         result.append('_');
/*     */       }
/*     */     }
/* 194 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.TypePath
 * JD-Core Version:    0.5.4
 */