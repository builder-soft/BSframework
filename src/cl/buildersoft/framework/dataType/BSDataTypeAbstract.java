package cl.buildersoft.framework.dataType;

import cl.buildersoft.framework.dataType.impl.BSBoolean;
import cl.buildersoft.framework.dataType.impl.BSCalendar;
import cl.buildersoft.framework.dataType.impl.BSDate;
import cl.buildersoft.framework.dataType.impl.BSDouble;
import cl.buildersoft.framework.dataType.impl.BSInteger;
import cl.buildersoft.framework.dataType.impl.BSLong;
import cl.buildersoft.framework.dataType.impl.BSTimestamp;

public  abstract class BSDataTypeAbstract {
	public Boolean isNumber() {
		return this instanceof BSDouble || this instanceof BSInteger || this instanceof BSLong;
	}

	public Boolean isTime() {
		return this instanceof BSDate || this instanceof BSTimestamp || this instanceof BSCalendar;
	}

	public Boolean isBoolean() {
		return this instanceof BSBoolean;
	}
}
