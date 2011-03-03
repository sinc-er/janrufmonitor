package de.janrufmonitor.ui.jface.application;

import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.filter.IFilterSerializer;

public interface IFilterManager extends IFilterSerializer {
	
	public String getFiltersToLabelText(IFilter[] f, int shorten);
	
}
