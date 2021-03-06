package net.sf.eclipsefp.haskell.debug.core.internal.debug;

import net.sf.eclipsefp.haskell.core.util.GHCiSyntax;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * value of an haskell binding
 * @author JP Moresmau
 *
 */
public class HaskellValue extends HaskellDebugElement implements IValue {
  private final String val;
  private final String type;

  public HaskellValue(final HaskellVariable var,final String val){
    super( var.getDebugTarget() );
    this.type=var.getReferenceTypeName();
    this.val=val;
  }

  public HaskellValue(final HaskellDebugTarget target,final String type,final String val){
    super( target );
    this.type=type;
    this.val=val;
  }

  public String getReferenceTypeName() {
   return type;
  }

  public String getValueString()  {
   return val;
  }

  public IVariable[] getVariables() {
    return new IVariable[0];
  }

  public boolean hasVariables() {
    return false;
  }

  public boolean isAllocated()  {
   return !GHCiSyntax.UNRESOLVED.equals(val);
  }


}
