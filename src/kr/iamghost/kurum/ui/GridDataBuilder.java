package kr.iamghost.kurum.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

public class GridDataBuilder {
	private GridData gridData;

	public GridDataBuilder() {
		gridData = new GridData();
	}
	
	public GridDataBuilder setMinimumWidth(int width) {
		gridData.minimumWidth = width;

		return this;
	}
	
	public GridDataBuilder setMinimumHeight(int height) {
		gridData.minimumHeight = height;
	
		return this;
	}
	
	public GridDataBuilder setWidth(int width) {
		gridData.widthHint = width;
		
		return this;
	}
	
	public GridDataBuilder setHeight (int height) {
		gridData.heightHint = height;
		
		return this;
	}
	
	public GridDataBuilder spanHorizontal(int cellNums) {
		gridData.horizontalSpan = cellNums;
		
		return this;
	}
	
	public GridDataBuilder fillHorizontal() {
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		return this;
	}

	public GridData create() {
		return gridData;
	}
}
