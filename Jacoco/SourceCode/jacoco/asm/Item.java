/*     */ package org.jacoco.asm;
/*     */ 
/*     */ final class Item
/*     */ {
/*     */   int index;
/*     */   int type;
/*     */   int intVal;
/*     */   long longVal;
/*     */   String strVal1;
/*     */   String strVal2;
/*     */   String strVal3;
/*     */   int hashCode;
/*     */   Item next;
/*     */ 
/*     */   Item()
/*     */   {
/*     */   }
/*     */ 
/*     */   Item(int index)
/*     */   {
/* 122 */     this.index = index;
/*     */   }
/*     */ 
/*     */   Item(int index, Item i)
/*     */   {
/* 134 */     this.index = index;
/* 135 */     this.type = i.type;
/* 136 */     this.intVal = i.intVal;
/* 137 */     this.longVal = i.longVal;
/* 138 */     this.strVal1 = i.strVal1;
/* 139 */     this.strVal2 = i.strVal2;
/* 140 */     this.strVal3 = i.strVal3;
/* 141 */     this.hashCode = i.hashCode;
/*     */   }
/*     */ 
/*     */   void set(int intVal)
/*     */   {
/* 151 */     this.type = 3;
/* 152 */     this.intVal = intVal;
/* 153 */     this.hashCode = (0x7FFFFFFF & this.type + intVal);
/*     */   }
/*     */ 
/*     */   void set(long longVal)
/*     */   {
/* 163 */     this.type = 5;
/* 164 */     this.longVal = longVal;
/* 165 */     this.hashCode = (0x7FFFFFFF & this.type + (int)longVal);
/*     */   }
/*     */ 
/*     */   void set(float floatVal)
/*     */   {
/* 175 */     this.type = 4;
/* 176 */     this.intVal = Float.floatToRawIntBits(floatVal);
/* 177 */     this.hashCode = (0x7FFFFFFF & this.type + (int)floatVal);
/*     */   }
/*     */ 
/*     */   void set(double doubleVal)
/*     */   {
/* 187 */     this.type = 6;
/* 188 */     this.longVal = Double.doubleToRawLongBits(doubleVal);
/* 189 */     this.hashCode = (0x7FFFFFFF & this.type + (int)doubleVal);
/*     */   }
/*     */ 
/*     */   void set(int type, String strVal1, String strVal2, String strVal3)
/*     */   {
/* 207 */     this.type = type;
/* 208 */     this.strVal1 = strVal1;
/* 209 */     this.strVal2 = strVal2;
/* 210 */     this.strVal3 = strVal3;
/* 211 */     switch (type)
/*     */     {
/*     */     case 7:
/* 213 */       this.intVal = 0;
/*     */     case 1:
/*     */     case 8:
/*     */     case 16:
/*     */     case 30:
/* 218 */       this.hashCode = (0x7FFFFFFF & type + strVal1.hashCode());
/* 219 */       return;
/*     */     case 12:
/* 221 */       this.hashCode = 
/* 222 */         (0x7FFFFFFF & type + strVal1.hashCode() * strVal2
/* 222 */         .hashCode());
/* 223 */       return;
/*     */     }
/*     */ 
/* 230 */     this.hashCode = 
/* 231 */       (0x7FFFFFFF & type + strVal1.hashCode() * strVal2
/* 231 */       .hashCode() * strVal3.hashCode());
/*     */   }
/*     */ 
/*     */   void set(String name, String desc, int bsmIndex)
/*     */   {
/* 246 */     this.type = 18;
/* 247 */     this.longVal = bsmIndex;
/* 248 */     this.strVal1 = name;
/* 249 */     this.strVal2 = desc;
/* 250 */     this.hashCode = 
/* 251 */       (0x7FFFFFFF & 18 + bsmIndex * this.strVal1
/* 251 */       .hashCode() * this.strVal2.hashCode());
/*     */   }
/*     */ 
/*     */   void set(int position, int hashCode)
/*     */   {
/* 265 */     this.type = 33;
/* 266 */     this.intVal = position;
/* 267 */     this.hashCode = hashCode;
/*     */   }
/*     */ 
/*     */   boolean isEqualTo(Item i)
/*     */   {
/* 281 */     switch (this.type) { case 1:
/*     */     case 7:
/*     */     case 8:
/*     */     case 16:
/*     */     case 30:
/* 287 */       return i.strVal1.equals(this.strVal1);
/*     */     case 5:
/*     */     case 6:
/*     */     case 32:
/* 291 */       return i.longVal == this.longVal;
/*     */     case 3:
/*     */     case 4:
/* 294 */       return i.intVal == this.intVal;
/*     */     case 31:
/* 296 */       return (i.intVal == this.intVal) && (i.strVal1.equals(this.strVal1));
/*     */     case 12:
/* 298 */       return (i.strVal1.equals(this.strVal1)) && (i.strVal2.equals(this.strVal2));
/*     */     case 18:
/* 300 */       return (i.longVal == this.longVal) && (i.strVal1.equals(this.strVal1)) && 
/* 301 */         (i.strVal2
/* 301 */         .equals(this.strVal2));
/*     */     case 2:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     case 13:
/*     */     case 14:
/*     */     case 15:
/*     */     case 17:
/*     */     case 19:
/*     */     case 20:
/*     */     case 21:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29: } return (i.strVal1.equals(this.strVal1)) && (i.strVal2.equals(this.strVal2)) && 
/* 309 */       (i.strVal3
/* 309 */       .equals(this.strVal3));
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.Item
 * JD-Core Version:    0.5.4
 */