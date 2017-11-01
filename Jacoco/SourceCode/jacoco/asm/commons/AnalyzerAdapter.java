/*     */ package org.jacoco.asm.commons;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.jacoco.asm.Handle;
/*     */ import org.jacoco.asm.Label;
/*     */ import org.jacoco.asm.MethodVisitor;
/*     */ import org.jacoco.asm.Opcodes;
/*     */ import org.jacoco.asm.Type;
/*     */ 
/*     */ public class AnalyzerAdapter extends MethodVisitor
/*     */ {
/*     */   public List<Object> locals;
/*     */   public List<Object> stack;
/*     */   private List<Label> labels;
/*     */   public Map<Object, Object> uninitializedTypes;
/*     */   private int maxStack;
/*     */   private int maxLocals;
/*     */   private String owner;
/*     */ 
/*     */   public AnalyzerAdapter(String owner, int access, String name, String desc, MethodVisitor mv)
/*     */   {
/* 144 */     this(327680, owner, access, name, desc, mv);
/* 145 */     if (super.getClass() != AnalyzerAdapter.class)
/* 146 */       throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   protected AnalyzerAdapter(int api, String owner, int access, String name, String desc, MethodVisitor mv)
/*     */   {
/* 171 */     super(api, mv);
/* 172 */     this.owner = owner;
/* 173 */     this.locals = new ArrayList();
/* 174 */     this.stack = new ArrayList();
/* 175 */     this.uninitializedTypes = new HashMap();
/*     */ 
/* 177 */     if ((access & 0x8) == 0) {
/* 178 */       if ("<init>".equals(name))
/* 179 */         this.locals.add(Opcodes.UNINITIALIZED_THIS);
/*     */       else {
/* 181 */         this.locals.add(owner);
/*     */       }
/*     */     }
/* 184 */     Type[] types = Type.getArgumentTypes(desc);
/* 185 */     for (int i = 0; i < types.length; ++i) {
/* 186 */       Type type = types[i];
/* 187 */       switch (type.getSort())
/*     */       {
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/* 193 */         this.locals.add(Opcodes.INTEGER);
/* 194 */         break;
/*     */       case 6:
/* 196 */         this.locals.add(Opcodes.FLOAT);
/* 197 */         break;
/*     */       case 7:
/* 199 */         this.locals.add(Opcodes.LONG);
/* 200 */         this.locals.add(Opcodes.TOP);
/* 201 */         break;
/*     */       case 8:
/* 203 */         this.locals.add(Opcodes.DOUBLE);
/* 204 */         this.locals.add(Opcodes.TOP);
/* 205 */         break;
/*     */       case 9:
/* 207 */         this.locals.add(types[i].getDescriptor());
/* 208 */         break;
/*     */       default:
/* 211 */         this.locals.add(types[i].getInternalName());
/*     */       }
/*     */     }
/* 214 */     this.maxLocals = this.locals.size();
/*     */   }
/*     */ 
/*     */   public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
/*     */   {
/* 220 */     if (type != -1) {
/* 221 */       throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
/*     */     }
/*     */ 
/* 225 */     if (this.mv != null) {
/* 226 */       this.mv.visitFrame(type, nLocal, local, nStack, stack);
/*     */     }
/*     */ 
/* 229 */     if (this.locals != null) {
/* 230 */       this.locals.clear();
/* 231 */       this.stack.clear();
/*     */     } else {
/* 233 */       this.locals = new ArrayList();
/* 234 */       this.stack = new ArrayList();
/*     */     }
/* 236 */     visitFrameTypes(nLocal, local, this.locals);
/* 237 */     visitFrameTypes(nStack, stack, this.stack);
/* 238 */     this.maxStack = Math.max(this.maxStack, this.stack.size());
/*     */   }
/*     */ 
/*     */   private static void visitFrameTypes(int n, Object[] types, List<Object> result)
/*     */   {
/* 243 */     for (int i = 0; i < n; ++i) {
/* 244 */       Object type = types[i];
/* 245 */       result.add(type);
/* 246 */       if ((type == Opcodes.LONG) || (type == Opcodes.DOUBLE))
/* 247 */         result.add(Opcodes.TOP);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void visitInsn(int opcode)
/*     */   {
/* 254 */     if (this.mv != null) {
/* 255 */       this.mv.visitInsn(opcode);
/*     */     }
/* 257 */     execute(opcode, 0, null);
/* 258 */     if ((((opcode < 172) || (opcode > 177))) && (opcode != 191))
/*     */       return;
/* 260 */     this.locals = null;
/* 261 */     this.stack = null;
/*     */   }
/*     */ 
/*     */   public void visitIntInsn(int opcode, int operand)
/*     */   {
/* 267 */     if (this.mv != null) {
/* 268 */       this.mv.visitIntInsn(opcode, operand);
/*     */     }
/* 270 */     execute(opcode, operand, null);
/*     */   }
/*     */ 
/*     */   public void visitVarInsn(int opcode, int var)
/*     */   {
/* 275 */     if (this.mv != null) {
/* 276 */       this.mv.visitVarInsn(opcode, var);
/*     */     }
/* 278 */     execute(opcode, var, null);
/*     */   }
/*     */ 
/*     */   public void visitTypeInsn(int opcode, String type)
/*     */   {
/* 283 */     if (opcode == 187) {
/* 284 */       if (this.labels == null) {
/* 285 */         Label l = new Label();
/* 286 */         this.labels = new ArrayList(3);
/* 287 */         this.labels.add(l);
/* 288 */         if (this.mv != null) {
/* 289 */           this.mv.visitLabel(l);
/*     */         }
/*     */       }
/* 292 */       for (int i = 0; i < this.labels.size(); ++i) {
/* 293 */         this.uninitializedTypes.put(this.labels.get(i), type);
/*     */       }
/*     */     }
/* 296 */     if (this.mv != null) {
/* 297 */       this.mv.visitTypeInsn(opcode, type);
/*     */     }
/* 299 */     execute(opcode, 0, type);
/*     */   }
/*     */ 
/*     */   public void visitFieldInsn(int opcode, String owner, String name, String desc)
/*     */   {
/* 305 */     if (this.mv != null) {
/* 306 */       this.mv.visitFieldInsn(opcode, owner, name, desc);
/*     */     }
/* 308 */     execute(opcode, 0, desc);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void visitMethodInsn(int opcode, String owner, String name, String desc)
/*     */   {
/* 315 */     if (this.api >= 327680) {
/* 316 */       super.visitMethodInsn(opcode, owner, name, desc);
/* 317 */       return;
/*     */     }
/* 319 */     doVisitMethodInsn(opcode, owner, name, desc, opcode == 185);
/*     */   }
/*     */ 
/*     */   public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
/*     */   {
/* 326 */     if (this.api < 327680) {
/* 327 */       super.visitMethodInsn(opcode, owner, name, desc, itf);
/* 328 */       return;
/*     */     }
/* 330 */     doVisitMethodInsn(opcode, owner, name, desc, itf);
/*     */   }
/*     */ 
/*     */   private void doVisitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
/*     */   {
/* 335 */     if (this.mv != null) {
/* 336 */       this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
/*     */     }
/* 338 */     if (this.locals == null) {
/* 339 */       this.labels = null;
/* 340 */       return;
/*     */     }
/* 342 */     pop(desc);
/* 343 */     if (opcode != 184) {
/* 344 */       Object t = pop();
/* 345 */       if ((opcode == 183) && (name.charAt(0) == '<'))
/*     */       {
/*     */         Object u;
/*     */         Object u;
/* 347 */         if (t == Opcodes.UNINITIALIZED_THIS)
/* 348 */           u = this.owner;
/*     */         else {
/* 350 */           u = this.uninitializedTypes.get(t);
/*     */         }
/* 352 */         for (int i = 0; i < this.locals.size(); ++i) {
/* 353 */           if (this.locals.get(i) == t) {
/* 354 */             this.locals.set(i, u);
/*     */           }
/*     */         }
/* 357 */         for (int i = 0; i < this.stack.size(); ++i) {
/* 358 */           if (this.stack.get(i) == t) {
/* 359 */             this.stack.set(i, u);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 364 */     pushDesc(desc);
/* 365 */     this.labels = null;
/*     */   }
/*     */ 
/*     */   public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object[] bsmArgs)
/*     */   {
/* 371 */     if (this.mv != null) {
/* 372 */       this.mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
/*     */     }
/* 374 */     if (this.locals == null) {
/* 375 */       this.labels = null;
/* 376 */       return;
/*     */     }
/* 378 */     pop(desc);
/* 379 */     pushDesc(desc);
/* 380 */     this.labels = null;
/*     */   }
/*     */ 
/*     */   public void visitJumpInsn(int opcode, Label label)
/*     */   {
/* 385 */     if (this.mv != null) {
/* 386 */       this.mv.visitJumpInsn(opcode, label);
/*     */     }
/* 388 */     execute(opcode, 0, null);
/* 389 */     if (opcode == 167) {
/* 390 */       this.locals = null;
/* 391 */       this.stack = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void visitLabel(Label label)
/*     */   {
/* 397 */     if (this.mv != null) {
/* 398 */       this.mv.visitLabel(label);
/*     */     }
/* 400 */     if (this.labels == null) {
/* 401 */       this.labels = new ArrayList(3);
/*     */     }
/* 403 */     this.labels.add(label);
/*     */   }
/*     */ 
/*     */   public void visitLdcInsn(Object cst)
/*     */   {
/* 408 */     if (this.mv != null) {
/* 409 */       this.mv.visitLdcInsn(cst);
/*     */     }
/* 411 */     if (this.locals == null) {
/* 412 */       this.labels = null;
/* 413 */       return;
/*     */     }
/* 415 */     if (cst instanceof Integer) {
/* 416 */       push(Opcodes.INTEGER);
/* 417 */     } else if (cst instanceof Long) {
/* 418 */       push(Opcodes.LONG);
/* 419 */       push(Opcodes.TOP);
/* 420 */     } else if (cst instanceof Float) {
/* 421 */       push(Opcodes.FLOAT);
/* 422 */     } else if (cst instanceof Double) {
/* 423 */       push(Opcodes.DOUBLE);
/* 424 */       push(Opcodes.TOP);
/* 425 */     } else if (cst instanceof String) {
/* 426 */       push("java/lang/String");
/* 427 */     } else if (cst instanceof Type) {
/* 428 */       int sort = ((Type)cst).getSort();
/* 429 */       if ((sort == 10) || (sort == 9))
/* 430 */         push("java/lang/Class");
/* 431 */       else if (sort == 11)
/* 432 */         push("java/lang/invoke/MethodType");
/*     */       else
/* 434 */         throw new IllegalArgumentException();
/*     */     }
/* 436 */     else if (cst instanceof Handle) {
/* 437 */       push("java/lang/invoke/MethodHandle");
/*     */     } else {
/* 439 */       throw new IllegalArgumentException();
/*     */     }
/* 441 */     this.labels = null;
/*     */   }
/*     */ 
/*     */   public void visitIincInsn(int var, int increment)
/*     */   {
/* 446 */     if (this.mv != null) {
/* 447 */       this.mv.visitIincInsn(var, increment);
/*     */     }
/* 449 */     execute(132, var, null);
/*     */   }
/*     */ 
/*     */   public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
/*     */   {
/* 455 */     if (this.mv != null) {
/* 456 */       this.mv.visitTableSwitchInsn(min, max, dflt, labels);
/*     */     }
/* 458 */     execute(170, 0, null);
/* 459 */     this.locals = null;
/* 460 */     this.stack = null;
/*     */   }
/*     */ 
/*     */   public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
/*     */   {
/* 466 */     if (this.mv != null) {
/* 467 */       this.mv.visitLookupSwitchInsn(dflt, keys, labels);
/*     */     }
/* 469 */     execute(171, 0, null);
/* 470 */     this.locals = null;
/* 471 */     this.stack = null;
/*     */   }
/*     */ 
/*     */   public void visitMultiANewArrayInsn(String desc, int dims)
/*     */   {
/* 476 */     if (this.mv != null) {
/* 477 */       this.mv.visitMultiANewArrayInsn(desc, dims);
/*     */     }
/* 479 */     execute(197, dims, desc);
/*     */   }
/*     */ 
/*     */   public void visitMaxs(int maxStack, int maxLocals)
/*     */   {
/* 484 */     if (this.mv != null) {
/* 485 */       this.maxStack = Math.max(this.maxStack, maxStack);
/* 486 */       this.maxLocals = Math.max(this.maxLocals, maxLocals);
/* 487 */       this.mv.visitMaxs(this.maxStack, this.maxLocals);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object get(int local)
/*     */   {
/* 494 */     this.maxLocals = Math.max(this.maxLocals, local + 1);
/* 495 */     return (local < this.locals.size()) ? this.locals.get(local) : Opcodes.TOP;
/*     */   }
/*     */ 
/*     */   private void set(int local, Object type) {
/* 499 */     this.maxLocals = Math.max(this.maxLocals, local + 1);
/* 500 */     while (local >= this.locals.size()) {
/* 501 */       this.locals.add(Opcodes.TOP);
/*     */     }
/* 503 */     this.locals.set(local, type);
/*     */   }
/*     */ 
/*     */   private void push(Object type) {
/* 507 */     this.stack.add(type);
/* 508 */     this.maxStack = Math.max(this.maxStack, this.stack.size());
/*     */   }
/*     */ 
/*     */   private void pushDesc(String desc) {
/* 512 */     int index = (desc.charAt(0) == '(') ? desc.indexOf(')') + 1 : 0;
/* 513 */     switch (desc.charAt(index))
/*     */     {
/*     */     case 'V':
/* 515 */       return;
/*     */     case 'B':
/*     */     case 'C':
/*     */     case 'I':
/*     */     case 'S':
/*     */     case 'Z':
/* 521 */       push(Opcodes.INTEGER);
/* 522 */       return;
/*     */     case 'F':
/* 524 */       push(Opcodes.FLOAT);
/* 525 */       return;
/*     */     case 'J':
/* 527 */       push(Opcodes.LONG);
/* 528 */       push(Opcodes.TOP);
/* 529 */       return;
/*     */     case 'D':
/* 531 */       push(Opcodes.DOUBLE);
/* 532 */       push(Opcodes.TOP);
/* 533 */       return;
/*     */     case '[':
/* 535 */       if (index == 0) {
/* 536 */         push(desc); return;
/*     */       }
/* 538 */       push(desc.substring(index, desc.length()));
/*     */ 
/* 540 */       break;
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'L':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'W':
/*     */     case 'X':
/*     */     case 'Y':
/*     */     default:
/* 543 */       if (index == 0) {
/* 544 */         push(desc.substring(1, desc.length() - 1)); return;
/*     */       }
/* 546 */       push(desc.substring(index + 1, desc.length() - 1));
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object pop()
/*     */   {
/* 552 */     return this.stack.remove(this.stack.size() - 1);
/*     */   }
/*     */ 
/*     */   private void pop(int n) {
/* 556 */     int size = this.stack.size();
/* 557 */     int end = size - n;
/* 558 */     for (int i = size - 1; i >= end; --i)
/* 559 */       this.stack.remove(i);
/*     */   }
/*     */ 
/*     */   private void pop(String desc)
/*     */   {
/* 564 */     char c = desc.charAt(0);
/* 565 */     if (c == '(') {
/* 566 */       int n = 0;
/* 567 */       Type[] types = Type.getArgumentTypes(desc);
/* 568 */       for (int i = 0; i < types.length; ++i) {
/* 569 */         n += types[i].getSize();
/*     */       }
/* 571 */       pop(n);
/* 572 */     } else if ((c == 'J') || (c == 'D')) {
/* 573 */       pop(2);
/*     */     } else {
/* 575 */       pop(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void execute(int opcode, int iarg, String sarg) {
/* 580 */     if (this.locals == null) {
/* 581 */       this.labels = null;
/* 582 */       return;
/*     */     }
/*     */ 
/* 585 */     switch (opcode)
/*     */     {
/*     */     case 0:
/*     */     case 116:
/*     */     case 117:
/*     */     case 118:
/*     */     case 119:
/*     */     case 145:
/*     */     case 146:
/*     */     case 147:
/*     */     case 167:
/*     */     case 177:
/* 596 */       break;
/*     */     case 1:
/* 598 */       push(Opcodes.NULL);
/* 599 */       break;
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*     */     case 16:
/*     */     case 17:
/* 609 */       push(Opcodes.INTEGER);
/* 610 */       break;
/*     */     case 9:
/*     */     case 10:
/* 613 */       push(Opcodes.LONG);
/* 614 */       push(Opcodes.TOP);
/* 615 */       break;
/*     */     case 11:
/*     */     case 12:
/*     */     case 13:
/* 619 */       push(Opcodes.FLOAT);
/* 620 */       break;
/*     */     case 14:
/*     */     case 15:
/* 623 */       push(Opcodes.DOUBLE);
/* 624 */       push(Opcodes.TOP);
/* 625 */       break;
/*     */     case 21:
/*     */     case 23:
/*     */     case 25:
/* 629 */       push(get(iarg));
/* 630 */       break;
/*     */     case 22:
/*     */     case 24:
/* 633 */       push(get(iarg));
/* 634 */       push(Opcodes.TOP);
/* 635 */       break;
/*     */     case 46:
/*     */     case 51:
/*     */     case 52:
/*     */     case 53:
/* 640 */       pop(2);
/* 641 */       push(Opcodes.INTEGER);
/* 642 */       break;
/*     */     case 47:
/*     */     case 143:
/* 645 */       pop(2);
/* 646 */       push(Opcodes.LONG);
/* 647 */       push(Opcodes.TOP);
/* 648 */       break;
/*     */     case 48:
/* 650 */       pop(2);
/* 651 */       push(Opcodes.FLOAT);
/* 652 */       break;
/*     */     case 49:
/*     */     case 138:
/* 655 */       pop(2);
/* 656 */       push(Opcodes.DOUBLE);
/* 657 */       push(Opcodes.TOP);
/* 658 */       break;
/*     */     case 50:
/* 660 */       pop(1);
/* 661 */       Object t1 = pop();
/* 662 */       if (t1 instanceof String)
/* 663 */         pushDesc(((String)t1).substring(1));
/*     */       else {
/* 665 */         push("java/lang/Object");
/*     */       }
/* 667 */       break;
/*     */     case 54:
/*     */     case 56:
/*     */     case 58:
/* 671 */       Object t1 = pop();
/* 672 */       set(iarg, t1);
/* 673 */       if (iarg > 0) {
/* 674 */         Object t2 = get(iarg - 1);
/* 675 */         if ((t2 == Opcodes.LONG) || (t2 == Opcodes.DOUBLE))
/* 676 */           set(iarg - 1, Opcodes.TOP); 
/* 676 */       }break;
/*     */     case 55:
/*     */     case 57:
/* 682 */       pop(1);
/* 683 */       Object t1 = pop();
/* 684 */       set(iarg, t1);
/* 685 */       set(iarg + 1, Opcodes.TOP);
/* 686 */       if (iarg > 0) {
/* 687 */         Object t2 = get(iarg - 1);
/* 688 */         if ((t2 == Opcodes.LONG) || (t2 == Opcodes.DOUBLE))
/* 689 */           set(iarg - 1, Opcodes.TOP); 
/* 689 */       }break;
/*     */     case 79:
/*     */     case 81:
/*     */     case 83:
/*     */     case 84:
/*     */     case 85:
/*     */     case 86:
/* 699 */       pop(3);
/* 700 */       break;
/*     */     case 80:
/*     */     case 82:
/* 703 */       pop(4);
/* 704 */       break;
/*     */     case 87:
/*     */     case 153:
/*     */     case 154:
/*     */     case 155:
/*     */     case 156:
/*     */     case 157:
/*     */     case 158:
/*     */     case 170:
/*     */     case 171:
/*     */     case 172:
/*     */     case 174:
/*     */     case 176:
/*     */     case 191:
/*     */     case 194:
/*     */     case 195:
/*     */     case 198:
/*     */     case 199:
/* 722 */       pop(1);
/* 723 */       break;
/*     */     case 88:
/*     */     case 159:
/*     */     case 160:
/*     */     case 161:
/*     */     case 162:
/*     */     case 163:
/*     */     case 164:
/*     */     case 165:
/*     */     case 166:
/*     */     case 173:
/*     */     case 175:
/* 735 */       pop(2);
/* 736 */       break;
/*     */     case 89:
/* 738 */       Object t1 = pop();
/* 739 */       push(t1);
/* 740 */       push(t1);
/* 741 */       break;
/*     */     case 90:
/* 743 */       Object t1 = pop();
/* 744 */       Object t2 = pop();
/* 745 */       push(t1);
/* 746 */       push(t2);
/* 747 */       push(t1);
/* 748 */       break;
/*     */     case 91:
/* 750 */       Object t1 = pop();
/* 751 */       Object t2 = pop();
/* 752 */       Object t3 = pop();
/* 753 */       push(t1);
/* 754 */       push(t3);
/* 755 */       push(t2);
/* 756 */       push(t1);
/* 757 */       break;
/*     */     case 92:
/* 759 */       Object t1 = pop();
/* 760 */       Object t2 = pop();
/* 761 */       push(t2);
/* 762 */       push(t1);
/* 763 */       push(t2);
/* 764 */       push(t1);
/* 765 */       break;
/*     */     case 93:
/* 767 */       Object t1 = pop();
/* 768 */       Object t2 = pop();
/* 769 */       Object t3 = pop();
/* 770 */       push(t2);
/* 771 */       push(t1);
/* 772 */       push(t3);
/* 773 */       push(t2);
/* 774 */       push(t1);
/* 775 */       break;
/*     */     case 94:
/* 777 */       Object t1 = pop();
/* 778 */       Object t2 = pop();
/* 779 */       Object t3 = pop();
/* 780 */       Object t4 = pop();
/* 781 */       push(t2);
/* 782 */       push(t1);
/* 783 */       push(t4);
/* 784 */       push(t3);
/* 785 */       push(t2);
/* 786 */       push(t1);
/* 787 */       break;
/*     */     case 95:
/* 789 */       Object t1 = pop();
/* 790 */       Object t2 = pop();
/* 791 */       push(t1);
/* 792 */       push(t2);
/* 793 */       break;
/*     */     case 96:
/*     */     case 100:
/*     */     case 104:
/*     */     case 108:
/*     */     case 112:
/*     */     case 120:
/*     */     case 122:
/*     */     case 124:
/*     */     case 126:
/*     */     case 128:
/*     */     case 130:
/*     */     case 136:
/*     */     case 142:
/*     */     case 149:
/*     */     case 150:
/* 809 */       pop(2);
/* 810 */       push(Opcodes.INTEGER);
/* 811 */       break;
/*     */     case 97:
/*     */     case 101:
/*     */     case 105:
/*     */     case 109:
/*     */     case 113:
/*     */     case 127:
/*     */     case 129:
/*     */     case 131:
/* 820 */       pop(4);
/* 821 */       push(Opcodes.LONG);
/* 822 */       push(Opcodes.TOP);
/* 823 */       break;
/*     */     case 98:
/*     */     case 102:
/*     */     case 106:
/*     */     case 110:
/*     */     case 114:
/*     */     case 137:
/*     */     case 144:
/* 831 */       pop(2);
/* 832 */       push(Opcodes.FLOAT);
/* 833 */       break;
/*     */     case 99:
/*     */     case 103:
/*     */     case 107:
/*     */     case 111:
/*     */     case 115:
/* 839 */       pop(4);
/* 840 */       push(Opcodes.DOUBLE);
/* 841 */       push(Opcodes.TOP);
/* 842 */       break;
/*     */     case 121:
/*     */     case 123:
/*     */     case 125:
/* 846 */       pop(3);
/* 847 */       push(Opcodes.LONG);
/* 848 */       push(Opcodes.TOP);
/* 849 */       break;
/*     */     case 132:
/* 851 */       set(iarg, Opcodes.INTEGER);
/* 852 */       break;
/*     */     case 133:
/*     */     case 140:
/* 855 */       pop(1);
/* 856 */       push(Opcodes.LONG);
/* 857 */       push(Opcodes.TOP);
/* 858 */       break;
/*     */     case 134:
/* 860 */       pop(1);
/* 861 */       push(Opcodes.FLOAT);
/* 862 */       break;
/*     */     case 135:
/*     */     case 141:
/* 865 */       pop(1);
/* 866 */       push(Opcodes.DOUBLE);
/* 867 */       push(Opcodes.TOP);
/* 868 */       break;
/*     */     case 139:
/*     */     case 190:
/*     */     case 193:
/* 872 */       pop(1);
/* 873 */       push(Opcodes.INTEGER);
/* 874 */       break;
/*     */     case 148:
/*     */     case 151:
/*     */     case 152:
/* 878 */       pop(4);
/* 879 */       push(Opcodes.INTEGER);
/* 880 */       break;
/*     */     case 168:
/*     */     case 169:
/* 883 */       throw new RuntimeException("JSR/RET are not supported");
/*     */     case 178:
/* 885 */       pushDesc(sarg);
/* 886 */       break;
/*     */     case 179:
/* 888 */       pop(sarg);
/* 889 */       break;
/*     */     case 180:
/* 891 */       pop(1);
/* 892 */       pushDesc(sarg);
/* 893 */       break;
/*     */     case 181:
/* 895 */       pop(sarg);
/* 896 */       pop();
/* 897 */       break;
/*     */     case 187:
/* 899 */       push(this.labels.get(0));
/* 900 */       break;
/*     */     case 188:
/* 902 */       pop();
/* 903 */       switch (iarg)
/*     */       {
/*     */       case 4:
/* 905 */         pushDesc("[Z");
/* 906 */         break;
/*     */       case 5:
/* 908 */         pushDesc("[C");
/* 909 */         break;
/*     */       case 8:
/* 911 */         pushDesc("[B");
/* 912 */         break;
/*     */       case 9:
/* 914 */         pushDesc("[S");
/* 915 */         break;
/*     */       case 10:
/* 917 */         pushDesc("[I");
/* 918 */         break;
/*     */       case 6:
/* 920 */         pushDesc("[F");
/* 921 */         break;
/*     */       case 7:
/* 923 */         pushDesc("[D");
/* 924 */         break;
/*     */       default:
/* 927 */         pushDesc("[J");
/* 928 */       }break;
/*     */     case 189:
/* 932 */       pop();
/* 933 */       pushDesc("[" + Type.getObjectType(sarg));
/* 934 */       break;
/*     */     case 192:
/* 936 */       pop();
/* 937 */       pushDesc(Type.getObjectType(sarg).getDescriptor());
/* 938 */       break;
/*     */     case 18:
/*     */     case 19:
/*     */     case 20:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29:
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/*     */     case 34:
/*     */     case 35:
/*     */     case 36:
/*     */     case 37:
/*     */     case 38:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/*     */     case 59:
/*     */     case 60:
/*     */     case 61:
/*     */     case 62:
/*     */     case 63:
/*     */     case 64:
/*     */     case 65:
/*     */     case 66:
/*     */     case 67:
/*     */     case 68:
/*     */     case 69:
/*     */     case 70:
/*     */     case 71:
/*     */     case 72:
/*     */     case 73:
/*     */     case 74:
/*     */     case 75:
/*     */     case 76:
/*     */     case 77:
/*     */     case 78:
/*     */     case 182:
/*     */     case 183:
/*     */     case 184:
/*     */     case 185:
/*     */     case 186:
/*     */     case 196:
/*     */     case 197:
/*     */     default:
/* 941 */       pop(iarg);
/* 942 */       pushDesc(sarg);
/*     */     }
/*     */ 
/* 945 */     this.labels = null;
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.commons.AnalyzerAdapter
 * JD-Core Version:    0.5.4
 */