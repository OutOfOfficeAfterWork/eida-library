package org.outofoffice.eida.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.outofoffice.lib.core.ui.EidaEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student implements EidaEntity<String> {
    private String studentCode;
    private String name;
    private String birthDate;
    private String gender;
    private Major major;
}
