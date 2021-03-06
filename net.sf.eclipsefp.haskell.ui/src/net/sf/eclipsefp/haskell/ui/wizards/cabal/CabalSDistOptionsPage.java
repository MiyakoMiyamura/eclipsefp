package net.sf.eclipsefp.haskell.ui.wizards.cabal;

import java.util.Collection;
import net.sf.eclipsefp.haskell.ui.internal.util.UITexts;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>Options for cabal sdist</p>
  *
  * @author JP Moresmau
 */
public class CabalSDistOptionsPage extends WizardPage {
  private DistFolder dFolder;
  private Button bSnapshot;
  private final Collection<IProject> projects;

  public CabalSDistOptionsPage(final Collection<IProject> projects) {
    super( "SDistOptions", UITexts.exportSource_options, null );
    this.projects=projects;
  }

  public void createControl( final Composite parent ) {
    initializeDialogUnits( parent );
    Composite composite = new Composite( parent, SWT.NONE );
    GridData gd=new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
    composite.setLayoutData( gd );
    int cols=projects.size()==1?3:2;
    GridLayout layout=new GridLayout( cols, false );
    composite.setLayout( layout );

    dFolder=new DistFolder(projects,composite, UITexts.exportSource_options_folder,UITexts.exportSource_options_folder_choose,UITexts.exportSource_options_folder_choose );

    bSnapshot=new Button(composite,SWT.CHECK);
    bSnapshot.setText( UITexts.exportSource_options_snapshot );
    gd=new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
    gd.horizontalSpan=3;
    bSnapshot.setLayoutData( gd );
    setControl( composite );
    Dialog.applyDialogFont( composite );
  }

  public String getFolder(){
    return dFolder.getFolder();
  }

  public boolean isSnapshot(){
    return bSnapshot.getSelection();
  }

}
