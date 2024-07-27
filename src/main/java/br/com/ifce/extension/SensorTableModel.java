package br.com.ifce.extension;

import br.com.ifce.model.Sensor;
import br.com.ifce.network.activemq.BrokerMediator;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class SensorTableModel extends AbstractTableModel {
    private final String[] columnNames = new String[]{
        "Topic", "Parameter", "Current Value", "Min Limit", "Max Limit"
    };

    private final Class[] columnClass = new Class[]{
        String.class, String.class, Double.class, Double.class, Double.class
    };

    public List<Sensor> getSensors() {
        return BrokerMediator.getInstance().getSensors();
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return this.columnClass[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public int getRowCount() {
        return this.getSensors().size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Sensor sensor = this.getSensors().get(rowIndex);

        return switch (columnIndex) {
            case 0 -> sensor.topicName();
            case 1 -> sensor.parameter();
            case 2 -> sensor.currentValue();
            case 3 -> sensor.minLimit();
            case 4 -> sensor.maxLimit();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        try {
            Sensor sensor = this.getSensors().get(rowIndex);
            Sensor updatedSensor = switch (columnIndex) {
                case 2 -> sensor.withCurrentValue((Double) value);
                case 3 -> sensor.withMinLimit((Double) value);
                case 4 -> sensor.withMaxLimit((Double) value);
                default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
            };

            BrokerMediator.getInstance().replaceSensor(rowIndex, updatedSensor);
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
