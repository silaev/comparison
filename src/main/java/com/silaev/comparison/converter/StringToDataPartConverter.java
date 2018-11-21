package com.silaev.comparison.converter;

import com.silaev.comparison.model.DataPart;
import java.beans.PropertyEditorSupport;

/**
 * Converts string to DataPart enum.
 */
public class StringToDataPartConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        setValue(DataPart.byPartName(text));
    }
}
