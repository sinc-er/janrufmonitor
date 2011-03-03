package de.janrufmonitor.ui.jface.application.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.io.PathResolver;

public class SearchAction extends AbstractAction {

	private class FindDialog extends Dialog {

		  private Viewer m_viewer;
			
		  // The find and replace buttons
		  private Button doFind;

		  /**
		   * FindReplaceDialog constructor
		   * 
		   * @param shell the parent shell
		   * @param document the associated document
		   * @param viewer the associated viewer
		   */
		  public FindDialog(Shell shell, Viewer viewer) {
		    super(shell, SWT.DIALOG_TRIM | SWT.MODELESS);
		    this.m_viewer = viewer;
		  }

		  /**
		   * Opens the dialog box
		   */
		  public void open() {
		    Shell shell = new Shell(getParent(), getStyle());
		    shell.setText(getI18nManager().getString(
		    	getNamespace(),
				"title",
				"label",
				getLanguage()
		    ));
		    shell.setImage(SWTImageManager.getInstance(
					getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
		    createContents(shell);
		    shell.pack();
		    shell.open();
		    Display display = getParent().getDisplay();
		    while (!shell.isDisposed()) {
		      if (!display.readAndDispatch()) {
		        display.sleep();
		      }
		    }
		  }

		  /**
		   * Performs a find
		   * 
		   * @param find the find string
		   * @param forward whether to search forward
		   * @param matchCase whether to match case
		   * @param wholeWord whether to search on whole word
		   * @param regexp whether find string is a regular expression
		   */
		  protected void doFind(String find, int offset) {
			  if (this.m_viewer instanceof TableViewer) {
			  	TableItem[] data = ((TableViewer)this.m_viewer).getTable().getItems();
			  	boolean first = true;
			  	if (data.length>offset) {
			  		TableItem c = null;
			  		((TableViewer)this.m_viewer).getTable().deselectAll();
			  		for (int i=offset;i<data.length;i++) {
			  			c = data[i];
			  			if (found(c, find)) {
			  				if (first)
			  					((TableViewer)this.m_viewer).getTable().setSelection(i);
			  				first = false;
			  				((TableViewer)this.m_viewer).getTable().select(i);
			  			}
			  		}	  			
			  	}				  
			  }
			  if (this.m_viewer instanceof TreeViewer) {
				  	((TreeViewer)this.m_viewer).expandAll();
				  	((TreeViewer)this.m_viewer).collapseAll();
				  	TreeItem[] data = ((TreeViewer)this.m_viewer).getTree().getItems();
				  	if (data.length>offset) {
				  		TreeItem c = null;
				  		((TreeViewer)this.m_viewer).getTree().deselectAll();
				  		List result = new ArrayList();
				  		for (int i=offset;i<data.length;i++) {
				  			c = data[i];
				  			if (c.getItemCount()>0) {				  				
				  				TreeItem[] subitems = c.getItems();
				  				for (int j=0;j<subitems.length;j++) {
				  					if (found(subitems[j], find) && !result.contains(c)) {
				  						c.setExpanded(true);
				  						result.add(c);
						  			}
				  				}
				  			}
				  			if (found(c, find)) {
				  				result.add(c);
				  			}
				  		}	  	
				  		
				  		if (result.size()==0) return;
				  		
				  		TreeItem[] items = new TreeItem[result.size()];
				  		for (int i=0;i<result.size();i++) {
				  			items[i] = (TreeItem) result.get(i);
				  		}
				  		
				  		((TreeViewer)this.m_viewer).getTree().setSelection(items);
				  	}				  
				  }
		  }
		  
		  private boolean found(TreeItem c, String text) {
			  	int cols = ((TreeViewer)this.m_viewer).getTree().getColumnCount();
			  	
			  	String content = null;
			  	for (int i=0;i<cols;i++) {
			  		content = c.getText(i).toLowerCase();
			  		if (content!=null) {
			  			if (content.indexOf(text.toLowerCase())>-1) return true;
			  		}
			  	}
			  	return false;
			  }
		  
		  private boolean found(TableItem c, String text) {
		  	int cols = ((TableViewer)this.m_viewer).getTable().getColumnCount();
		  	
		  	String content = null;
		  	for (int i=0;i<cols;i++) {
		  		content = c.getText(i).toLowerCase();
		  		if (content!=null) {
		  			if (content.indexOf(text.toLowerCase())>-1) return true;
		  		}
		  	}
		  	return false;
		  }
		  

		  /**
		   * Creates the dialog's contents
		   * 
		   * @param shell
		   */
		  protected void createContents(final Shell shell) {
		    shell.setLayout(new GridLayout(2, false));

		    // Add the text input fields
		    Composite text = new Composite(shell, SWT.NONE);
		    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		    text.setLayout(new GridLayout(3, true));

		    new Label(text, SWT.LEFT).setText(
	    		getI18nManager().getString(
    		    	getNamespace(),
    				"search",
    				"label",
    				getLanguage()
    		    )
		    );
		    final Text findText = new Text(text, SWT.BORDER);
		    GridData data = new GridData(GridData.FILL_HORIZONTAL);
		    data.horizontalSpan = 2;
		    findText.setLayoutData(data);

		    // Add the buttons
		    Composite buttons = new Composite(shell, SWT.NONE);
		    buttons.setLayout(new GridLayout(2, true));

		    // Create the Find button
		    doFind = new Button(buttons, SWT.PUSH);
		    doFind.setText(
	    		getI18nManager().getString(
    		    	getNamespace(),
    				"title",
    				"label",
    				getLanguage()
    		    )		
		    );
		    doFind.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


		    // Create the Close button
		    Button close = new Button(buttons, SWT.PUSH);
		    close.setText(
	    		getI18nManager().getString(
    		    	getNamespace(),
    				"close",
    				"label",
    				getLanguage()
    		    )	
		    );
		    close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		    close.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		        shell.close();
		      }
		    });

		    // Do a find
		    doFind.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		      	if (!m_viewer.getControl().isDisposed())
		      		doFind(findText.getText(), 0);
		      	else
		      		shell.close();
		      }
		    });

		    // Set defaults
		    findText.setFocus();
		    shell.setDefaultButton(doFind);
		  }
		}
	
	private static String NAMESPACE = "ui.jface.application.action.SearchAction";
	
	private IRuntime m_runtime;

	public SearchAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setImageDescriptor(new ApplicationImageDescriptor(
			PathResolver.getInstance(this.getRuntime()).getImageDirectory() + "search.gif"
		));
		this.setAccelerator(SWT.CTRL+'F');
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "search";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		FindDialog frd = new FindDialog(this.m_app.getApplication().getShell(), this.m_app.getApplication().getViewer());
		frd.open();
	}
	
	
}
