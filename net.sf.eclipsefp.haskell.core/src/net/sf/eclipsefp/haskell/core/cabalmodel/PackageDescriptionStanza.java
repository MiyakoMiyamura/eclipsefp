// Copyright (c) 2006 by Leif Frenzel <himself@leiffrenzel.de>
// All rights reserved.
package net.sf.eclipsefp.haskell.core.cabalmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/** <p>represents a stanza in a package description.</p>
  *
  * @author Leif Frenzel
  */
public class PackageDescriptionStanza {
  private static class CabalSyntaxMap<V> extends LinkedHashMap<String, V>{
    /**
    *
    */
   private static final long serialVersionUID = -630958988149146289L;

   @Override
   public V get(final Object key) {
     if (key instanceof String){
       return super.get(((String)key).toLowerCase());
     } else if (key instanceof CabalSyntax){
       return super.get(((CabalSyntax)key).getCabalName().toLowerCase());
     }
     return null;
   }

 }

  private final String name;
  private final int startLine;
  private int endLine;
  private int indent;
  private final Map<String, String> properties=new CabalSyntaxMap<String>();
  private final Map<String, ValuePosition> positions=new CabalSyntaxMap<ValuePosition>();
  private final CabalSyntax type;

  private final List<PackageDescriptionStanza> stanzas=new LinkedList<PackageDescriptionStanza>();


  PackageDescriptionStanza(final CabalSyntax type,final String name,
      final int startLine){
    this.type=type;
    this.name = name;
    this.startLine = startLine;
  }

  PackageDescriptionStanza( final CabalSyntax type,
                            final String name,
                            final int startLine,
                            final int endLine ) {
    this.type=type;
    this.name = name;
    this.startLine = startLine;
    this.endLine = endLine;
  }


  public void setEndLine( final int endLine ) {
    this.endLine = endLine;
  }

  public List<PackageDescriptionStanza> getStanzas() {
    return stanzas;
  }

  public CabalSyntax getType() {
    return type;
  }


  public int getIndent() {
    return indent;
  }


  public void setIndent( final int indent ) {
    this.indent = indent;
  }

  public Map<String, String> getProperties() {
    return properties;
  }


  public Map<String, ValuePosition> getPositions() {
    return positions;
  }


  /*public ValuePosition update(final CabalSyntax field,final String value){
    return update(field.getCabalName(),value);
  }

  public ValuePosition update(final String field,final String value){
    getProperties().put(field.toLowerCase(),value);
    ValuePosition vp=getPositions().get( field.toLowerCase() );
    if (vp==null){
      return new ValuePosition(endLine,endLine,0);
    }
    return vp;
  }*/

  public RealValuePosition update(final CabalSyntax field,String value){
    if (value!=null && value.trim().length()==0){
      value=null;
    }
    if (value!=null){
      getProperties().put(field.getCabalName().toLowerCase(),value);
    } else {
      getProperties().remove(field.getCabalName().toLowerCase());
    }
    ValuePosition oldVP=getPositions().get( field );
    int indent=0;
    int subIndent=0;
    StringBuilder sb=new StringBuilder();
    if (oldVP==null){
      oldVP=new ValuePosition(getEndLine(),getEndLine(),0);
      Collection<ValuePosition> vps=getPositions().values();
      for (int a=0;a<getIndent();a++){
        sb.append( ' ');
      }
      sb.append( field.getCabalName() );
      sb.append( ":" ); //$NON-NLS-1$
      int spaces=4;
      if (vps.isEmpty()){
        subIndent=getIndent()+field.getCabalName().length()+5;
      } else {
        ValuePosition vp=vps.iterator().next();
        indent=subIndent=vp.getSubsequentIndent();
        spaces=indent-(field.getCabalName().length() +1);
      }
      spaces=Math.max( spaces, 1 );
      for (int a=0;a<spaces;a++){
        sb.append( ' ');
      }
    } else {
      subIndent=oldVP.getSubsequentIndent();
      indent=oldVP.getInitialIndent();
    }
    if (value==null){
      getPositions().remove( field.getCabalName().toLowerCase() );
      return new RealValuePosition( oldVP,""); //$NON-NLS-1$
    }

    BufferedReader br=new BufferedReader( new StringReader( value ) );
    try {

      String line=br.readLine();
      int count=0;
      while (line!=null){
        for (int a=0;count>0 && a<subIndent;a++){
          sb.append( ' ');
        }
        count++;
        if (line.trim().length()==0){
          line= "."+line ; //$NON-NLS-1$
        }
        sb.append( line );
        sb.append( System.getProperty( "line.separator" ) ); //$NON-NLS-1$
        line=br.readLine();
      }
      if (count>1){
        for (int a=0;a<subIndent;a++){
          sb.insert( 0, ' ');
        }
        sb.insert( 0, System.getProperty( "line.separator" ) ); //$NON-NLS-1$

      }
      ValuePosition newVP=new ValuePosition(oldVP.getStartLine(),oldVP.getStartLine()+count,indent);
      newVP.setSubsequentIndent( subIndent );
      getPositions().put( field.getCabalName().toLowerCase(), newVP );
      return new RealValuePosition( oldVP,sb.toString());
    } catch (IOException ioe){
      return null;
    }
  }


  public RealValuePosition addToPropertyList(final CabalSyntax field,final String value){
   String s=getProperties().get( field );
   List<String> ls=PackageDescriptionLoader.parseList( s );
   // short lists we hope
   if (!ls.contains( value )){
     StringBuilder newValue=new StringBuilder();
     newValue.append( s );
     if (!value.trim().endsWith( "," )){ //$NON-NLS-1$
       newValue.append(","); //$NON-NLS-1$
     }
     if (!value.endsWith( " " )){ //$NON-NLS-1$
       newValue.append(" "); //$NON-NLS-1$
     }
     newValue.append(value);
     return update(field, newValue.toString() );
   }
   return update(field, s);
  }

  public RealValuePosition removeFromPropertyList(final CabalSyntax field,final String value){
    String s=getProperties().get( field );
    List<String> ls=PackageDescriptionLoader.parseList( s );
    // short lists we hope
    if (ls.contains( value )){
      StringBuilder newValue=new StringBuilder();
      for (String token:ls){
        if (!value.equals( token )){
          if(newValue.length()>0){
            newValue.append( ", " ); //$NON-NLS-1$
          }
          newValue.append( token);
        }
      }
      return update(field, newValue.toString() );
    }
    return update(field, s);
   }



  public String getName() {
    return name;
  }

  public int getStartLine() {
    return startLine;
  }

  public int getEndLine() {
    return endLine;
  }

  public String toTypeName(){
    return String.valueOf( getType() ) + (getName()!=null?" "+getName():"");  //$NON-NLS-1$//$NON-NLS-2$
  }

  // interface methods of Object
  //////////////////////////////

  @Override
  public String toString() {
    return   (getName()!=null?getName():String.valueOf( getType() ))
           + " (line " //$NON-NLS-1$
           + startLine
           + "-" //$NON-NLS-1$
           + endLine
           + ")"; //$NON-NLS-1$
  }

  public Collection<String> getDependentPackages(){
    Collection<String> ret=new HashSet<String>();
    String val=getProperties().get( CabalSyntax.FIELD_BUILD_DEPENDS);
    if (val!=null && val.length()>0){
      List<String> ls=PackageDescriptionLoader.parseList( val,"," ); //$NON-NLS-1$
      for (String s:ls){
        int a=0;
        for (;a<s.length();a++){
          char c=s.charAt( a );
          if (c == '=' || c== '>' || c== '<'){
            break;
          }
        }
        s=s.substring(0,a).trim();
        ret.add( s);
      }
    }
    return ret;
  }

  public Collection<String> getSourceDirs(){
    Collection<String> ret=new HashSet<String>();
    String val=getProperties().get( CabalSyntax.FIELD_HS_SOURCE_DIRS);
    if (val!=null && val.length()>0){
      ret.addAll(PackageDescriptionLoader.parseList( val));
    }
    // backward compatibility
    val=getProperties().get( "hs-source-dir"); //$NON-NLS-1$
    if (val!=null && val.length()>0){
      ret.addAll(PackageDescriptionLoader.parseList( val));
    }
    if (ret.isEmpty() && (getType()!=null && (getType().equals( CabalSyntax.SECTION_EXECUTABLE)
        || getType().equals( CabalSyntax.SECTION_LIBRARY)))){
      ret.add("."); //$NON-NLS-1$
    }
    return ret;
   }

}
