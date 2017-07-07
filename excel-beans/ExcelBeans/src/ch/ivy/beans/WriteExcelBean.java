/**
 * 
 */
package ch.ivy.beans;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Label;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.IIvyScriptEditor;
import ch.ivyteam.ivy.process.extension.IProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.ivy.scripting.objects.Recordset;

import org.apache.poi.hssf.usermodel.*;


/**
 * This Programm Interface Bean writes a recordset into an excel file 
 * @author bb
 */
public class WriteExcelBean extends AbstractUserProcessExtension {

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
		String rsParam = getConfigurationProperty("rs");
		String filepathParam = getConfigurationProperty("filepath").replace("\"", "");

		Recordset rs = (Recordset) getProcessDataField(context, rsParam);		
		String filePath = getFilePath(context, filepathParam);

		try (OutputStream out = new FileOutputStream(filePath))
		{
			// Evaluate call parameter
			// create a new workbook
			@SuppressWarnings("resource")
			HSSFWorkbook wb = new HSSFWorkbook();
			// create a new sheet
			HSSFSheet s = wb.createSheet();
			// declare a row object reference
			HSSFRow r = null;
			// declare a cell object reference
			HSSFCell c = null;
			// create cell style
			HSSFCellStyle cs = wb.createCellStyle();
			// create header row
			r = s.createRow(0);
			List<String> colnames = rs.getKeys();
			for (int i = 0; i < colnames.size(); i++) 
			{
				c = r.createCell(i);
				c.setCellStyle(cs);
				c.setCellValue(colnames.get(i));
			}
			// create rows
			for (int j = 0; j < rs.size(); j++) 
			{
				int rownum = (j + 1);
				// create a row
				r = s.createRow(rownum);

				for (int i = 0; i < colnames.size(); i++) 
				{
					int cellnum = (i);

					// create a string cell
					c = r.createCell(cellnum);
					c.setCellStyle(cs);
					// set the cell's string value: c.setEncoding( HSSFCell.ENCODING_COMPRESSED_UNICODE);
					Object obj = rs.getField(j, colnames.get(i));
					if (obj != null) 
					{
						try 
						{
							if (!Double.valueOf(obj.toString()).isNaN()) 
							{ 
								// write as double value
								c.setCellValue(Double.valueOf(obj.toString())
										.doubleValue());
							}
						} 
						catch (Exception ex) 
						{ 
							// write as string value
							c.setCellValue(obj.toString());
						}
					}
				}
			}
			
			// write the workbook to the output stream
			// close workbook and file
			wb.write(out);
			//wb.close();
		
		} 
		catch (Exception ex) 
		{
			Ivy.log().error(
					"WriteExcelBean failed to write output file! Source attribute ="
							+ rsParam + ". Output file path =" + filePath	+ ".");
			ex.printStackTrace();
			throw ex;
		}
		return in;
	}

	private String getFilePath(IIvyScriptContext context, String filepathParam)
			throws IvyScriptException {
		String filePath;
		if(filepathParam.startsWith("in."))
		{	
			filePath = (String) getProcessDataField(context, filepathParam);
		}
		else
		{
			filePath = filepathParam;
		}
		return filePath;
	}

	/**
	 * @author bb
	 *
	 */
	public static class Editor extends
			AbstractProcessExtensionConfigurationEditor 
		{

		private IIvyScriptEditor rsField;
		private IIvyScriptEditor filepathField;

		/*
		 * @see ch.ivyteam.ivy.process.extension.impl.
		 * AbstractProcessExtensionConfigurationEditor
		 * #createEditorPanelContent(java.awt.Container,
		 * ch.ivyteam.ivy.process.extension
		 * .IProcessExtensionConfigurationEditorEnvironment)
		 */
		@Override
		protected void createEditorPanelContent(
				Container editorPanel,
				IProcessExtensionConfigurationEditorEnvironment editorEnvironment) 
		{

			rsField = editorEnvironment.createIvyScriptEditor();
			editorPanel.add(new Label("Recordset attribute"), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0,
							0), 0, 0));
			editorPanel.add(rsField.getComponent(),
					new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));
			filepathField = editorEnvironment.createIvyScriptEditor();
			editorPanel.add(new Label("File path"), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0,
							0), 0, 0));			
			editorPanel.add(filepathField.getComponent(),
					new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 0, 0));			
		}

		/*
		 * @see ch.ivyteam.ivy.process.extension.impl.
		 * AbstractProcessExtensionConfigurationEditor
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

			// save the data from the editor to the configuration
			setBeanConfigurationProperty("filepath", filepathField.getText());
			setBeanConfigurationProperty("rs", rsField.getText());			
			return true;
		}
	}
}
