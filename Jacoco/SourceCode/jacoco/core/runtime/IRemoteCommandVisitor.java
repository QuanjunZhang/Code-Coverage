package org.jacoco.core.runtime;

import java.io.IOException;

public abstract interface IRemoteCommandVisitor
{
  public abstract void visitDumpCommand(boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
}

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.runtime.IRemoteCommandVisitor
 * JD-Core Version:    0.5.4
 */