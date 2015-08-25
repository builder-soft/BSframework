package cl.buildersoft.framework.dataType;

import cl.buildersoft.framework.dataType.impl.BSBoolean;
import cl.buildersoft.framework.dataType.impl.BSCalendar;
import cl.buildersoft.framework.dataType.impl.BSDate;
import cl.buildersoft.framework.dataType.impl.BSDecimal;
import cl.buildersoft.framework.dataType.impl.BSDouble;
import cl.buildersoft.framework.dataType.impl.BSInteger;
import cl.buildersoft.framework.dataType.impl.BSLong;
import cl.buildersoft.framework.dataType.impl.BSString;
import cl.buildersoft.framework.dataType.impl.BSTimestamp;
import cl.buildersoft.framework.exception.BSProgrammerException;

/**
 * <code>
 * http://www.tutorialspoint.com/design_pattern/factory_pattern.htm
</code>
 */
public class BSDataTypeFactory {
	public BSDataType create(BSDataTypeEnum dataType) {
		BSDataType out = null;

		if (dataType.equals(BSDataTypeEnum.BOOLEAN)) {
			out = new BSBoolean();
		} else if (dataType.equals(BSDataTypeEnum.CALENDAR)) {
			out = new BSCalendar();
		} else if (dataType.equals(BSDataTypeEnum.DATE)) {
			out = new BSDate();
		} else if (dataType.equals(BSDataTypeEnum.DECIMAL)) {
			out = new BSDecimal();
		} else if (dataType.equals(BSDataTypeEnum.DOUBLE)) {
			out = new BSDouble();
		} else if (dataType.equals(BSDataTypeEnum.INTEGER)) {
			out = new BSInteger();
		} else if (dataType.equals(BSDataTypeEnum.LONG)) {
			out = new BSLong();
		} else if (dataType.equals(BSDataTypeEnum.STRING)) {
			out = new BSString();
		} else if (dataType.equals(BSDataTypeEnum.TEXT)) {
			out = new BSString();
		} else if (dataType.equals(BSDataTypeEnum.TIMESTAMP)) {
			out = new BSTimestamp();
		} else {
			throw new BSProgrammerException("Type " + dataType.toString() + " not exists");
		}

		return out;
	}

}
