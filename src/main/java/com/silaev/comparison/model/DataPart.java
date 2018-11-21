package com.silaev.comparison.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum DataPart {
    LEFT("left"), RIGHT("right");

    private final String part;

    public static DataPart byPartName(String partName){
        Objects.requireNonNull(partName);

        for (DataPart dataPart: values()) {
            if(dataPart.getPart().equalsIgnoreCase(partName)){
                return dataPart;
            }
        }
        throw new IllegalArgumentException(String.format(
                "Cannot find a data part by %s", partName));
    }
}
