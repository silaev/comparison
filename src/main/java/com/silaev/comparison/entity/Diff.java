package com.silaev.comparison.entity;

import com.silaev.comparison.model.DataPart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A document for storing some diff data.
 * Employs @CompoundIndex to prevent data duplication.
 */
@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "userId_dataPart",
                def = "{'userId' : 1, 'dataPart': 1}",
                unique = true)})
@EqualsAndHashCode(of = {"userId", "dataPart"})
public class Diff extends BaseEntity {
    private Integer userId;
    private DataPart dataPart;
    private String data;
}
