package y.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import y.yReady;
import y.utils.GeneralProperties;
import y.utils.GeneralPropertiesExporter;
import y.utils.Utils;

public class GeneralPropertiesTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -5694966155637270964L;
	
	private List<String> keys;
	private List<String> types;
	private List<String> values;
	
	public GeneralPropertiesTableModel() {
		keys = new ArrayList<String>();
		types = new ArrayList<String>();
		values = new ArrayList<String>();
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return values.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		switch (arg1) {
			case 0 : return keys.get(arg0);
			case 1 : return types.get(arg0);
			default:
			case 2 : return values.get(arg0);
		}
	}
	
	@Override
    public String getColumnName(int column) {
		switch (column) {
			case 0 : return "Field";
			case 1 : return "Type";
			default:
			case 2 : return "Value";
		}
    }
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int column) {
		if (column == 0)
			keys.set(rowIndex, (String) aValue);
		else if (column == 1)
			types.set(rowIndex, (String) aValue);
		else if (column == 2)
			values.set(rowIndex, (String) aValue);
	}
	
//	public int getRow(String prop) {
//		for (int i=0; i<keys.size(); i++)
//			if (keys.get(i).equals(prop))
//				return i;
//		
//		return -1;
//	}
	
    @Override
    public Class<? extends Object> getColumnClass(int column) {
    	return String.class;
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
    	return column != 0;
    }
    	
	public void clearData() {
		keys.clear();
		types.clear();
		values.clear();
		
		this.fireTableDataChanged();
	}
	
	
	
	public void refresh(GeneralProperties<String> config) {
		keys.clear();
		types.clear();
		values.clear();

		final Map<Class<?>, Map<String, ?>> map = config.get();
		
		for (Class<?> type : map.keySet()) {
			
			final Map<String, ?> mapvalues = map.get(type);
			
			for (String key : mapvalues.keySet()) {
				final String value = mapvalues.get(key).toString();
				
				keys.add(key);
				types.add(typeToString(type));
				values.add(value);
			}
		}
		
		this.fireTableDataChanged();
	}
	
	private static String typeToString(Class<?> type) {
		final String s = type.toString();
		return s.startsWith("class ") ? s.substring(6) : s;
	}
	
	
	public void save(GeneralProperties<String> config) {
		config.clear();

		for (int i=0, imax=keys.size(); i<imax; i++) {
			final String key = keys.get(i);
			final String type = types.get(i);
			final String value = values.get(i);
			
			try {
				final Class<?> cType = Class.forName(type);
				config.set(cType, key, value);
			}
			catch (Exception e) {
				Utils.MessageBox(e.getMessage()+"\n"+e.toString(), "Internal error");
			}
		}
		
		try {
			GeneralPropertiesExporter.save(config, yReady.CONFIG_FILENAME);
		}
		catch (Exception e) {
			Utils.MessageBox("Error saving conf.\n"+e.getMessage()+"\n"+e.toString(), "Internal error");
		}			
	}
}
