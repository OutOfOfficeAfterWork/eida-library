package org.outofoffice.eida.domain;

import lombok.*;
import org.outofoffice.lib.core.ui.EidaEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Major implements EidaEntity<String> {
    private String majorName;
    private String englishName;
}
