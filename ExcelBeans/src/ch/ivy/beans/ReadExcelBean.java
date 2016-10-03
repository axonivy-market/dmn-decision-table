/**
 * 
 */
package ch.ivy.beans;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Label;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.IIvyScriptEditor;
import ch.ivyteam.ivy.process.extension.IProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.ivy.scripting.objects.Record;
import ch.ivyteam.ivy.scripting.objects.Recordset;

/**
 * This Programm Interface Bean reads a recordset from an excel file 
 * @author bb
 *
 */ 
public class ReadExcelBean extends AbstractUserProcessExtension {

	private boolean addTitleRow = false;

	/*
	 * @see
	 * ch.ivyteam.ivy.process.extension.IUserProcessExtension#perform(ch.ivyteam
	 * .ivy.process.engine.IRequestId,
	 * ch.ivyteam.ivy.scripting.objects.CompositeObject,
	 * ch.ivyteam.ivy.scripting.language.IIvyScriptContext)
	 */
	@Override
	public CompositeObject perform(IRequestId requestId, CompositeObject in,
			IIvyScriptContext context) throws Exception 
	{
		// configuration
		String rsParam = null;
		String filepathParam = null;

		Recordset rs = null;
		String filePath = null;

		FileInputStream input = null;
		POIFSFileSystem fs = null;
		HSSFWorkbook wb = null;

		try {
			// Evaluate call parameter
			rsParam = getConfigurationProperty("rs");
			rs = (Recordset) getProcessDataField(context, rsParam);

			filepathParam = getConfigurationProperty("filepath").replace("\"",
					"");
			if (filepathParam.startsWith("in.")) 
			{
				filePath = (String) getProcessDataField(context, filepathParam);
			} 
			else 
			{
				filePath = filepathParam;
			}
			
			// read excel file
		    input = new FileInputStream(filePath);
			fs = new POIFSFileSystem(input);
			wb = new HSSFWorkbook(fs); 
			HSSFSheet sheet = wb.getSheetAt(0);

			// Iterate over each row in the sheet
			Iterator<Row> rows = sheet.rowIterator();
			int columns = countColumns(sheet.rowIterator());

			String[] columnTitles = createColumnTitles(columns,
					sheet.rowIterator());

			rs = new Recordset(columnTitles); 

			if (!addTitleRow) 
			{
				// first row read as column titles
				rows.next();
			} 
			else 
			{
				Ivy.log().warn("ReadExcelBean: No title row found.");
			}
			
			while (rows.hasNext()) 
			{
				HSSFRow row = (HSSFRow) rows.next();

				// Iterate over each cell in the row and print out the cell's
				// content
				Iterator<Cell> cells = row.cellIterator();
				Object[] cellValues = new Object[columns];
				while (cells.hasNext()) 
				{
					HSSFCell cell = (HSSFCell) cells.next();
					if (cell.getColumnIndex() < columns) 
					{
						switch (cell.getCellType()) 
						{
						case HSSFCell.CELL_TYPE_NUMERIC:
							cellValues[cell.getColumnIndex()] = new Double(
									cell.getNumericCellValue());
							break;
						case HSSFCell.CELL_TYPE_STRING:
							cellValues[cell.getColumnIndex()] = cell
									.getStringCellValue();
							break;
						default:
							cellValues[cell.getColumnIndex()] = null;
							break;
						}
					}
				}
				rs.add(new Record(columnTitles, cellValues));
			}

			//wb.close();
			
		} catch (Exception ex) 
		{
			Ivy.log().error("ReadExcelBean failed! Source file =" + filePath
							+ ". Target recordset =" + rsParam + ".");
			ex.printStackTrace(); 
			throw ex;
		} finally 
		{
			IOUtils.closeQuietly(input);
		}

		// Store result in process data
		setProcessDataField(context, rsParam, rs);
		return in;
	}

	private String[] createColumnTitles(int numberOfColumns, Iterator<Row> rows) 
	{
		String[] columnTitleSet = new String[numberOfColumns];
		int i = 0;
		HSSFRow row = (HSSFRow) rows.next();
		Iterator<Cell> cells = row.cellIterator();
		addTitleRow = false;
		if (checkCellEntries(row.cellIterator())) 
		{
			while (cells.hasNext()) 
			{
				HSSFCell currentCell = (HSSFCell) cells.next();
				if (currentCell.getColumnIndex() < numberOfColumns) 
				{
					columnTitleSet[currentCell.getColumnIndex()] = currentCell
							.getStringCellValue();
					i++;
					addTitleRow = false;
				}
			}
		} 
		else 
		{
			while (cells.hasNext() && i < numberOfColumns) 
			{
				cells.next();
				columnTitleSet[i] = "Column" + (i + 1);
				i++;
			}
			addTitleRow = true;
		}
		return columnTitleSet;
	}

	/*
	 * check the type of the first row 
	 * @return true if the first row contains labels
	 */
	private boolean checkCellEntries(Iterator<Cell> cells) 
	{
		boolean type = false;
		while (cells.hasNext()) 
		{
			HSSFCell cell = (HSSFCell) cells.next();
			switch (cell.getCellType()) 
			{
			case HSSFCell.CELL_TYPE_NUMERIC:
				type = false;
				break;

			case HSSFCell.CELL_TYPE_STRING:
				if (cell.getStringCellValue().length() < 100) 
				{
					type = true;
				} 
				else 
				{
					type = false;
				}
				break;

			default:
				type = false;
				break;
			}
			if (!type) 
			{
				return false;
			}
		}
		return true;
	}

	private int countColumns(Iterator<Row> rows) 
	{
		HSSFRow row = (HSSFRow) rows.next();
		Iterator<Cell> cells = row.cellIterator();
		int i = 0;
		while (cells.hasNext()) 
		{
			i++;
			cells.next();
		}
		return i;
	}

	public static class Editor extends
			AbstractProcessExtensionConfigurationEditor 
			{

		private IIvyScriptEditor rsField;
		private IIvyScriptEditor filepathField;

		/*
		 * @see ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor
		 * #createEditorPanelContent(java.awt.Container, ch.ivyteam.ivy.process.extension
		 * .IProcessExtensionConfigurationEditorEnvironment)
		 */
		@Override
		protected void createEditorPanelContent(
				Container editorPanel,
				IProcessExtensionConfigurationEditorEnvironment editorEnvironment) 
		{

			rsField = editorEnvironment.createIvyScriptEditor();
			editorPanel.add(new Label("Recordset attribute"),
					new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0,
							0));
			editorPanel.add(rsField.getComponent(),
					new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
			filepathField = editorEnvironment.createIvyScriptEditor();
			editorPanel.add(new Label("File path"), new GridBagConstraints(0,
					1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			editorPanel.add(filepathField.getComponent(),
					new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
		}

		/*
		 * @see ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor
		 * #loadUiDataFromConfiguration()
		 */
		@Override
		protected void loadUiDataFromConfiguration() 
		{

			// load data from the configuration into the editor
			filepathField.setText(getBeanConfigurationProperty("filepath"));
			rsField.setText(getBeanConfigurationProperty("rs"));
		}

		/*
		 * @see ch.ivyteam.ivy.process.extension.impl.
		 * AbstractProcessExtensionConfigurationEditor
		 * #saveUiDataToConfiguration()
		 */
		@Override
		protected boolean saveUiDataToConfiguration() 
		{
			// save the data from the editor into the configuration
			setBeanConfigurationProperty("filepath", filepathField.getText());
			setBeanConfigurationProperty("rs", rsField.getText());
			return true;
		}
	}
}
