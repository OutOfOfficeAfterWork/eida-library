package org.outofoffice.eida.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.outofoffice.lib.core.ui.EidaEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject implements EidaEntity<String> {
    private String subjectName;
    private String englishName;
    private Major major;
}
